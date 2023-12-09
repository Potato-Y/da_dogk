package com.github.da_dogk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.da_dogk.R
import com.github.da_dogk.server.response.User

class SchoolAdapter: RecyclerView.Adapter<SchoolAdapter.SchoolViewHolder>() {

    private var schools: List<User> = emptyList()

    // 새로운 아이템 뷰 홀더를 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchoolViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.group_member_item, parent, false)
        return SchoolViewHolder(view)
    }

    // 뷰 홀더에 데이터를 바인딩
    override fun onBindViewHolder(holder:SchoolViewHolder, position: Int) {
        val school = schools[position]
        holder.bind(school)
    }

    // 데이터 세트의 크기 반환
    override fun getItemCount(): Int = schools.size

    // 그룹 목록을 설정하고 어댑터를 갱신하는 함수
    fun setMember(schools: List<User>) {
        this.schools = schools
        notifyDataSetChanged()
    }

    // 각 아이템 뷰의 뷰 홀더 클래스 정의
    class SchoolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        private val userName: TextView = itemView.findViewById(R.id.tv_member_name)
        private val userTime: TextView = itemView.findViewById(R.id.tv_member_time)


        // 뷰 홀더에 데이터를 바인딩하는 함수
        fun bind(member: User) {
            userName.text = member.nickname

            // todayStudyTime을 시:분:초 형식으로 변환하여 categoryTime에 표시
            val formattedTime = convertSecondsToFormattedTime(member.todayStudyTime)
            userTime.text = formattedTime
        }

        // 초를 시:분:초 형식으로 변환하는 함수
        private fun convertSecondsToFormattedTime(seconds: Int): String {
            val hours = seconds / 3600
            val minutes = (seconds % 3600) / 60
            val remainingSeconds = seconds % 60

            return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
        }
    }
}