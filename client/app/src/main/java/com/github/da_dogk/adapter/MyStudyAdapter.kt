package com.github.da_dogk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.da_dogk.R


class Data(val profile:Int, val name:String)

//viewholder - 여러개가 띄워질때 한화면에 보이는 부분, v가 List를 의미
class MyStudyViewHolder(v : View) : RecyclerView.ViewHolder(v) {
//    val profile = v.IB_~
//    val name = v.TV_~
}

class MyStudyAdapter(val DataList:ArrayList<Data>) : RecyclerView.Adapter<MyStudyViewHolder>() {

    //생성하는 부분
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyStudyViewHolder {
        val cellForRow = LayoutInflater.from(parent.context).inflate(R.layout.my_study_item,parent,false)
        return MyStudyViewHolder(cellForRow)
    }

    //viewholder 갯수설정 부분, 능동적으로 갯수 바뀌도록
    override fun getItemCount(): Int {
        return DataList.size
    }

    //수정하는 부분, 클릭시 이벤트 여기서 만들자(시간 측정)
    override fun onBindViewHolder(holder: MyStudyViewHolder, position: Int) {

//        holder.profile.setImageResource(DataList[position].profile)
//        holder.name.text = DataList[position].name
//        holder.itemView.setOnClickListener(v ->
//            Toask~~~
//        )

    }

}