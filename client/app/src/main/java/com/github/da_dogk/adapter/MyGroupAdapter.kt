package com.github.da_dogk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.da_dogk.R
import com.github.da_dogk.server.response.GroupGenerateResponse

class MyGroupAdapter : RecyclerView.Adapter<MyGroupAdapter.MyGroupViewHolder>() {

    private var myGroups: List<GroupGenerateResponse> = emptyList()

    // 새로운 아이템 뷰 홀더를 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyGroupViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.my_group_item, parent, false)
        return MyGroupViewHolder(view)
    }

    // 뷰 홀더에 데이터를 바인딩
    override fun onBindViewHolder(holder: MyGroupViewHolder, position: Int) {
        val myGroup = myGroups[position]
        holder.bind(myGroup)
    }

    // 데이터 세트의 크기 반환
    override fun getItemCount(): Int = myGroups.size

    // 그룹 목록을 설정하고 어댑터를 갱신하는 함수
    fun setMyGroup(myGroups: List<GroupGenerateResponse>) {
        this.myGroups= myGroups
        notifyDataSetChanged()
    }

    // 각 아이템 뷰의 뷰 홀더 클래스 정의
    class MyGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        private val groupName: TextView = itemView.findViewById(R.id.TV_group_name)
        private val groupAvgTime: TextView = itemView.findViewById(R.id.TV_avg_time)


        fun bind(myGroup: GroupGenerateResponse) {
            groupName.text = myGroup.groupName

//            val formattedTime = convertSecondsToFormattedTime(myGroup.)
//            groupAvgTime.text = formattedTime
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