package com.github.da_dogk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.da_dogk.R
import com.github.da_dogk.server.response.GroupAvgTimeResponse
import com.github.da_dogk.server.response.GroupGenerateResponse

class MyGroupAdapter : RecyclerView.Adapter<MyGroupAdapter.MyGroupViewHolder>() {

    private var myGroups: List<GroupGenerateResponse> = emptyList()


    private var groupClickListener: OnGroupClickListener? = null

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

        holder.itemView.setOnClickListener {
            groupClickListener?.onGroupClick(myGroup)
        }
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

            // Null 체크 추가
            val avgResponse = myGroup.groupAvgTimeResponse
            if (avgResponse != null) {
                val avgTime = avgResponse.averageTime
                val formattedTime = convertSecondsToFormattedTime(avgTime)
                groupAvgTime.text = formattedTime
            } else {
                // 처리할 내용 추가 (예: 특별한 텍스트 설정 또는 빈 값으로 설정)
                groupAvgTime.text = "N/A"
            }
        }

        // 초를 시:분:초 형식으로 변환하는 함수
        private fun convertSecondsToFormattedTime(seconds: Int): String {
            val hours = seconds / 3600
            val minutes = (seconds % 3600) / 60
            val remainingSeconds = seconds % 60

            return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
        }
    }
    interface OnGroupClickListener {
        fun onGroupClick(group: GroupGenerateResponse)
    }
    fun setOnGroupClickListener(listener: OnGroupClickListener) {
        groupClickListener = listener
    }
}