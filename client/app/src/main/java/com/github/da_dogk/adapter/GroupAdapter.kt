package com.github.da_dogk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.da_dogk.R
import com.github.da_dogk.server.response.GroupGenerateResponse

// RecyclerView 어댑터 클래스 정의
class GroupAdapter : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    private var groups: List<GroupGenerateResponse> = emptyList()

    // 새로운 아이템 뷰 홀더를 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.group_item2, parent, false)
        return GroupViewHolder(view)
    }

    // 뷰 홀더에 데이터를 바인딩
    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groups[position]
        holder.bind(group)
    }

    // 데이터 세트의 크기 반환
    override fun getItemCount(): Int = groups.size

    // 그룹 목록을 설정하고 어댑터를 갱신하는 함수
    fun setGroups(groups: List<GroupGenerateResponse>) {
        this.groups = groups
        notifyDataSetChanged()
    }

    // 각 아이템 뷰의 뷰 홀더 클래스 정의
    class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val groupName: TextView = itemView.findViewById(R.id.tv_group_name)
        private val groupIntro: TextView = itemView.findViewById(R.id.tv_group_intro)

        // 뷰 홀더에 데이터를 바인딩하는 함수
        fun bind(group: GroupGenerateResponse) {
            groupName.text = group.groupName
            groupIntro.text = group.groupIntro
        }
    }
}