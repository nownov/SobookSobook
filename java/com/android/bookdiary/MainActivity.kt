/*
어시슈트 - 소북소북 코드입니다.

정보보호학과 2020111323 김지원
정보보호학과 2021111325 김해린
정보보호학과 2021111336 송다은(팀 대표)
정보보호학과 2021111694 이가연

 */

package com.android.bookdiary


import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainFragment = MainFragment()
        supportFragmentManager.beginTransaction().replace(R.id.container, mainFragment).commit()//  첫 화면 mainfragment로 설정
        val navigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)//   bottomNavigationView 위젯 설정
        navigationView.setOnNavigationItemSelectedListener(this)//  아이템 선택시 동작
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.tab1 -> {//    탭 1 누르면 통계화면(monthlyFragment)
                val monthlyFragment = MonthlyFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, monthlyFragment)
                    .commit()
            }
            R.id.tab2 -> {//    탭 2 누르면 메인화면(MainFragment)
                val mainFragment = MainFragment()

                supportFragmentManager.beginTransaction().replace(R.id.container, mainFragment)
                    .commit()
            }
            R.id.tab3 -> {//    탭 3 누르면 도서목록화면(ListFragment)
                val listFragment = ListFragment()
                supportFragmentManager.beginTransaction().replace(R.id.container, listFragment)
                    .commit()
            }
        }
        return true

    }
}




