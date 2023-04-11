package ru.niv.bible.basic.component;

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
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;

import java.util.ArrayList;
import java.util.List;

import ru.niv.bible.MainActivity;

public class Payment {

    private final Status listener;
    private final Context context;
    private BillingClient billingClient;
    private SkuDetails skuDetails;
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
        final BillingClient finalBillingClient = billingClient;
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                Log.d(Static.log,"Не удалось присоединиться");
                connectGooglePlayBilling(false);
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    finalBillingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP, (billingResult1, list) -> {
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
        List<String> skuList = new ArrayList<>();
        skuList.add(product);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(params.build(),
                (billingResult, skuDetailsList) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                        Log.d(Static.log,"Узнаем цены");
                        for (SkuDetails skuDetails : skuDetailsList) {
                            if (skuDetails.getSku().equals(product)) {
                                setSkuDetails(skuDetails);
                                if (launch) launchPurchaseFlow();
                            }
                        }
                    }

                });
    }

    public void launchPurchaseFlow() {
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();
        billingClient.launchBillingFlow((MainActivity) context, billingFlowParams);
    }

    public void verifyPayment(Purchase purchase) {
        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();

        ConsumeResponseListener responseListener = (billingResult, s) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                if (purchase.getSkus().get(0).equals(product)) {
                    Log.d(Static.log,"Купили только что, прячем рекламу");
                    listener.verifyPayment();
                }
            }

        };

        billingClient.consumeAsync(consumeParams, responseListener);
    }

    private void setSkuDetails(SkuDetails skuDetails) {
        this.skuDetails = skuDetails;
    }

    public void resume() {
        billingClient.queryPurchasesAsync(
                BillingClient.SkuType.INAPP,
                (billingResult, list) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        for (Purchase purchase : list) {
                            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                                verifyPayment(purchase);
                            }
                        }
                    }
                }
        );
    }

    public SkuDetails getSkuDetails() {
        return skuDetails;
    }

}
