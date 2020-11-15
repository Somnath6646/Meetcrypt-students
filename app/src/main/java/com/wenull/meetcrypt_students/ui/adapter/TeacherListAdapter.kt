package com.wenull.meetcrypt_students.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wenull.meetcrypt_students.R
import com.wenull.meetcrypt_students.data.main.MainViewModel
import com.wenull.meetcrypt_students.databinding.TeacherListLayoutBinding
import com.wenull.meetcrypt_students.utils.models.Event
import com.wenull.meetcrypt_students.utils.models.STATUS
import com.wenull.meetcrypt_students.utils.models.Teacher


class TeacherListAdapter(private val viewModel: MainViewModel): RecyclerView.Adapter<TeacherListViewHolder>(), Filterable{

    val teacherList = ArrayList<Teacher>()
    val teacherListAll = ArrayList<Teacher>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<TeacherListLayoutBinding>(inflater, R.layout.teacher_list_layout, parent, false)
        return TeacherListViewHolder(binding)
    }

    override fun getItemCount(): Int = teacherList.size

    override fun onBindViewHolder(holder: TeacherListViewHolder, position: Int) {
        holder.bind(teacherList[position], viewModel)
    }


    fun setList(teacherList: List<Teacher>){
        this.teacherList.clear()
        this.teacherList.addAll(teacherList)
        this.teacherListAll.clear()
        this.teacherListAll.addAll(teacherList)
        notifyDataSetChanged()
    }


    override fun getFilter(): Filter {
        return filter
    }

    private var filter = object: Filter(){
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            var result = FilterResults()
            var filteredList = ArrayList<Teacher>()

            filteredList.addAll(teacherListAll)
            result.values = filteredList

            if(constraint != null){
                result.values = filteredList.filter {
                    it.username.toLowerCase().contains(constraint.toString().toLowerCase())
                }
            }else if(constraint?.length == 0){
                teacherList.addAll(teacherListAll)
            }
            return result
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            teacherList.clear()
            teacherList.addAll(results?.values as List<Teacher>)
            notifyDataSetChanged()
        }
    }

}






class TeacherListViewHolder(private val binding: TeacherListLayoutBinding): RecyclerView.ViewHolder(binding.root){
    fun bind(teacher: Teacher, viewModel: MainViewModel){
        binding.teacherUsername.text = teacher.username
        binding.followUnfollowBtn.text = if(teacher.status == STATUS.FOLLOWING) "Unfollow" else "Follow"
        binding.followUnfollowBtn.setOnClickListener {
            if(teacher.status == STATUS.FOLLOWING){
                viewModel.unfollow(teacher)
            }else{
                viewModel.follow(teacher)
            }
        }
    }
}