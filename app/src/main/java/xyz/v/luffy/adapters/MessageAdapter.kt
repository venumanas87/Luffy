package xyz.v.luffy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.bot_text.view.*
import xyz.v.luffy.R
import xyz.v.luffy.models.Chat
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(private val chatList:List<Chat>):RecyclerView.Adapter<MessageAdapter.mvh>() {


    class mvh(view: View):RecyclerView.ViewHolder(view){
        val botCard:RelativeLayout = view.findViewById(R.id.bot_card)
        val  userCard:RelativeLayout = view.findViewById(R.id.userr_text_card)
        val dateCard:CardView = view.findViewById(R.id.dateCard)
        val message:TextView = view.findViewById(R.id.message)
        val time:TextView = view.findViewById(R.id.timemsg)
        val messageB:TextView = view.findViewById(R.id.messageB)
        val timeB:TextView = view.findViewById(R.id.timemsgB)
        val dateCardTimeTv:TextView = view.findViewById(R.id.dateCard_timeTV)
        val dateDayTv:TextView = view.findViewById(R.id.day_date_tv)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mvh {
        var chat = LayoutInflater.from(parent.context).inflate(R.layout.sent_text,parent,false)
        when(viewType){

            1->{
           chat = LayoutInflater.from(parent.context).inflate(R.layout.bot_text,parent,false)
                return mvh(chat)
            }
            2->{
                chat = LayoutInflater.from(parent.context).inflate(R.layout.bot_text,parent,false)
                return mvh(chat)
            }
            3->{
                chat = LayoutInflater.from(parent.context).inflate(R.layout.bot_text,parent,false)
                return mvh(chat)
            }


        }
        return mvh(chat)
    }

    override fun onBindViewHolder(holder: mvh, position: Int) {

        val obj = chatList[position]
        val timeFormat = SimpleDateFormat("hh:mm a")
        when(obj.isUser){
            1->{
                holder.botCard.visibility = View.GONE
                holder.userCard.visibility = View.VISIBLE
                holder.message.text = obj.message
                holder.time.text = timeFormat.format(Date())
            }
            2->{
                holder.botCard.visibility = View.VISIBLE
                holder.messageB.text = obj.message
                holder.timeB.text = timeFormat.format(Date())
            }
            3->{
                holder.botCard.visibility = View.INVISIBLE
                holder.dateCard.visibility = View.VISIBLE
                holder.dateCardTimeTv.text = timeFormat.format(Date())
            }
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun getItemViewType(position: Int): Int {
        return chatList[position].isUser
    }
}