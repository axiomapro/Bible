package ru.ampstudy.bible.component.immutable.box;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchaseHistoryParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList;

import java.util.List;

import ru.ampstudy.bible.MainActivity;

public class Payment {

    private final Status listener;
    private final Context context;
    private BillingClient billingClient;
    private ProductDetails productDetails;
    private final String product = "nivbible1"; // product ID для совершения покупки

    public interface Status {
        void paid();
        void notPaid(boolean launch);
        void verifyPayment();
    }

    public Payment(Context context,Status listener) {
        this.context = context;
        this.listener = listener;
    }

    public void initializeBillingClient() {
        billingClient = BillingClient.newBuilder(context)
                .setListener(new PurchasesUpdatedListener() {
                    @Override
                    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                            for (Purchase purchase : list) {
                                verifyPayment(purchase);
                            }
                        }

                    }
                })
                .enablePendingPurchases()
                .build();
    }

    public void connectGooglePlayBilling(boolean launch) {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                Log.d(Static.log,"Не удалось присоединиться");
                connectGooglePlayBilling(false);
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    billingClient.queryPurchaseHistoryAsync(QueryPurchaseHistoryParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(), (billingResult1, list) -> {
                        if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null && list.size() > 0) {
                            Log.d(Static.log,"Приложение куплено");
                            listener.paid();
                        } else {
                            Log.d(Static.log,"Приложение не куплено");
                            listener.notPaid(launch);
                        }
                    });
                }
            }
        });
    }

    public void getProducts(boolean launch) {
        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(
                                ImmutableList.of(
                                        QueryProductDetailsParams.Product.newBuilder()
                                                .setProductId(product)
                                                .setProductType(BillingClient.ProductType.INAPP)
                                                .build()))
                        .build();

        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                new ProductDetailsResponseListener() {
                    public void onProductDetailsResponse(BillingResult billingResult,
                                                         List<ProductDetails> productDetailsList) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && productDetailsList != null) {
                            Log.d(Static.log,"Узнаем цены");
                            for (ProductDetails productDetails : productDetailsList) {
                                if (productDetails.getProductId().equals(product)) {
                                    Payment.this.productDetails = productDetails;
                                    if (launch) launchPurchaseFlow();
                                }
                            }
                        }
                    }
                }
        );
    }

    public void launchPurchaseFlow() {
        ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                ImmutableList.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .build()
                );

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();

        billingClient.launchBillingFlow((MainActivity) context, billingFlowParams);
    }

    public void verifyPayment(Purchase purchase) {
        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();

        ConsumeResponseListener responseListener = (billingResult, s) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                if (purchase.getProducts().get(0).equals(product)) {
                    Log.d(Static.log,"Купили только что, прячем рекламу");
                    listener.verifyPayment();
                }
            }

        };

        billingClient.consumeAsync(consumeParams, responseListener);
    }

    public void resume() {
        if (billingClient == null) return;
        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(),
                new PurchasesResponseListener() {
                    public void onQueryPurchasesResponse(
                            BillingResult billingResult,
                            List<Purchase> purchases) {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                for (Purchase purchase : purchases) {
                                    if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                                        verifyPayment(purchase);
                                    }
                                }
                            }
                    }
                }
        );
    }

    public ProductDetails getProductDetails() {
        return productDetails;
    }

}