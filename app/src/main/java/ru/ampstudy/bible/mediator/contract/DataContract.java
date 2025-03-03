package ru.ampstudy.bible.mediator.contract;

public interface DataContract {

    interface Main {
        void data(int id,String type);
    }

    interface ChapterAndPageMain {
        void data(int id,int page,String chapter);
    }

    interface ReadingPlanContainer {
        void data(int type,boolean status,String name,String text,String start,String notification);
    }

    interface ReadingPlanMaterial {
        void data(int type,String name,String start,String finish);
    }

    interface DailyVerse {
        void data(int chapter,int page,int position,String chapterName,String text);
    }
}