package xyz.v.luffy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.bot_text.view.*
import xyz.v.luffy.R
import xyz.v.luffy.models.Chat

class MessageAdapter(private val chatList:List<Chat>):RecyclerView.Adapter<MessageAdapter.mvh>() {


    class mvh(view: View):RecyclerView.ViewHolder(view){
       val message:TextView = view.findViewById(R.id.message)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mvh {
        var chat = LayoutInflater.from(parent.context).inflate(R.layout.sent_text,parent,false)
        when(viewType){

            1->{
           chat = LayoutInflater.from(parent.context).inflate(R.layout.sent_text,parent,false)
                return mvh(chat)
            }
            2->{
                chat = LayoutInflater.from(parent.context).inflate(R.layout.bot_text,parent,false)
                return mvh(chat)
            }


        }
        return mvh(chat)
    }

    override fun onBindViewHolder(holder: mvh, position: Int) {
        val obj = chatList[position]
        holder.message.text = obj.message
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when(chatList[position].isUser){
            true->{
                1
            }
            false->{
                2
            }
            //3->{return 3 }

        }
    }
}