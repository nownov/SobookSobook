/*
어시슈트 - 소북소북 코드입니다.

정보보호학과 2020111323 김지원
정보보호학과 2021111325 김해린
정보보호학과 2021111336 송다은(팀 대표)
정보보호학과 2021111694 이가연

 */

package com.android.bookdiary

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DBManager( //4
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    // DB 생성
    override fun onCreate(db: SQLiteDatabase?) {

        // 책 색깔, 책 제목, 작가, 총 페이지 수, 최근에 읽은 페이지의 수, 현재까지 읽은 페이지 수의 합을 저장할 bookDB 테이블 생성
        db!!.execSQL("CREATE TABLE bookDB (color CHAR(20) NOT NULL, title CHAR(30) PRIMARY KEY, author CHAR(30), totalPage INTEGER, nowPage INTEGER, accumPage INTEGER)")

        // KOSIS 국가통계포털/국내통계/국민독서실태조사/독서율과 독서량/종이책+전자책 독서량(성인) 2021년 자료 에서 가져온 성별, 연령별 독서 평균 자료를 저장한 readingDB테이블 생성
        db!!.execSQL("CREATE TABLE readingDB (category CHAR(20), cases INTEGER, totalAvg INTEGER, readAvg INTEGER)")

        // 매일 작성한 독후감의 페이지 수, 마음에 든 문장, 내 생각, 날짜, 책 제목, 책 색깔, 책 총 페이지 수를 저장할 writeDB 테이블 생성
        db!!.execSQL("CREATE TABLE writeDB (dNowPage INTEGER, dSentence CHAR(100), dThink CHAR(100), dDate CHAR(20), dTitle CHAR(30), dColor CHAR(20), dTotalPage INTEGER)")

    }

    // DB 수정
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS bookDB")
        db!!.execSQL("DROP TABLE IF EXISTS readingDB")
        db!!.execSQL("DROP TABLE IF EXISTS writeDB")
        onCreate(db)


    }


    }

