/*
어시슈트 - 소북소북 코드입니다.

정보보호학과 2020111323 김지원
정보보호학과 2021111325 김해린
정보보호학과 2021111336 송다은(팀 대표)
정보보호학과 2021111694 이가연

 */

package com.android.bookdiary

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment()  {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase
    lateinit var btnAdd: Button
    lateinit var viewPager: ViewPager
    lateinit var tabLayout: TabLayout

    @SuppressLint("UseRequireInsteadOfGet", "Range")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        
        viewPager = view.findViewById(R.id.viewPager)
        tabLayout = view.findViewById(R.id.tabLayout)

        btnAdd = view.findViewById(R.id.btnAdd) // 책 추가 버튼
        btnAdd.setOnClickListener {
            val bookRegFragment = BookRegFragment()
            val transaction : FragmentTransaction = fragmentManager!!.beginTransaction()
            transaction.replace(R.id.container, bookRegFragment)
            transaction.commit()
        }

        // 탭 레이아웃에 따라 전체 리스트, 완독한 책의 리스트, 독서를 진행 중인 책의 리스트, 아직 읽지 않은 책의 리스트를 볼 수 있도록 뷰페이저 설정
        // 에뮬레이터의 로딩 속도가 느려 책장 탭을 눌렀을 때 화면이 제대로 보이지 않는 경우가 간혹 있습니다. 상단 탭을 여러 번 오가면 다시 화면이 제대로 보입니다.
        val adapter = ViewPagerAdapter(activity?.supportFragmentManager!!)
        adapter.addFragment(AllFragment(), "All")
        adapter.addFragment(EdFragment(), "-ed")
        adapter.addFragment(IngFragment(), "-ing")
        adapter.addFragment(WillFragment(), "will")
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

        return view
    }
}