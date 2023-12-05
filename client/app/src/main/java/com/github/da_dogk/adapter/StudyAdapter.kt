package com.github.da_dogk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.da_dogk.R
import com.github.da_dogk.server.response.MyStudyResponse

// RecyclerView 어댑터 클래스 정의
class StudyAdapter : RecyclerView.Adapter<StudyAdapter.StudyViewHolder>() {

    private var studys: List<MyStudyResponse> = emptyList()

    // 새로운 아이템 뷰 홀더를 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.my_study_item, parent, false)
        return StudyViewHolder(view)
    }

    // 뷰 홀더에 데이터를 바인딩
    override fun onBindViewHolder(holder: StudyViewHolder, position: Int) {
        val study = studys[position]
        holder.bind(study)
    }

    // 데이터 세트의 크기 반환
    override fun getItemCount(): Int = studys.size

    // 그룹 목록을 설정하고 어댑터를 갱신하는 함수
    fun setStudy(studys: List<MyStudyResponse>) {
        this.studys = studys
        notifyDataSetChanged()
    }

    // 각 아이템 뷰의 뷰 홀더 클래스 정의
    class StudyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        private val categoryName: TextView = itemView.findViewById(R.id.TV_category)
        private val categoryTime: TextView = itemView.findViewById(R.id.TV_study_time)


        // 뷰 홀더에 데이터를 바인딩하는 함수
        fun bind(study: MyStudyResponse) {
            categoryName.text = study.title

            // todayStudyTime을 시:분:초 형식으로 변환하여 categoryTime에 표시
            val formattedTime = convertSecondsToFormattedTime(study.user.todayStudyTime)
            categoryTime.text = formattedTime
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