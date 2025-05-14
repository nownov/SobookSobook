/*
어시슈트 - 소북소북 코드입니다.

정보보호학과 2020111323 김지원
정보보호학과 2021111325 김해린
정보보호학과 2021111336 송다은(팀 대표)
정보보호학과 2021111694 이가연

 */

package com.android.bookdiary

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter

// 탭 레이아웃에 따라 화면을 전환하는 뷰페이저 어댑터
class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val fragmentList = ArrayList<Fragment>()
    private val titleList = ArrayList<String>()

    override fun getCount(): Int = titleList.size   //현재 아이템의 개수 반환

    override fun getItem(position: Int): Fragment = fragmentList[position]  // 인자로 받은 위치의 데이터를 반환

    override fun getPageTitle(position: Int): CharSequence? = titleList[position]   // 위치에 따라 페이지 제목을 지정

    fun addFragment(fragment: Fragment, title: String){
        fragmentList.add(fragment)
        titleList.add(title)
    }
}