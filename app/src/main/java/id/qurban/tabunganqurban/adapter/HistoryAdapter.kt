package id.qurban.tabunganqurban.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.qurban.tabunganqurban.R
import id.qurban.tabunganqurban.data.Transaction
import id.qurban.tabunganqurban.databinding.ItemTransactionHistoryBinding
import id.qurban.tabunganqurban.ui.detail.pending.DetailPendingNabungActivity
import id.qurban.tabunganqurban.utils.FormatHelper.formatCurrencyDouble
import id.qurban.tabunganqurban.utils.FormatHelper.formatCurrencyString

class HistoryAdapter(private val context: Context) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    inner class HistoryViewHolder(private val binding: ItemTransactionHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: Transaction) {
            binding.apply {
                tvIdTransaksi.text = context.getString(R.string.id_transaction, transaction.transactionId)
                tvNominalTransaksi.text = formatCurrencyDouble(transaction.amount) // Format nominal
                tvStatus.text = transaction.status
                tvDateTransaksi.text = transaction.dateCreated // Tanggal sesuai format WIB
                // Tambahkan style warna status jika diperlukan
                when (transaction.status.lowercase()) {
                    "pending" -> tvStatus.setTextColor(context.getColor(R.color.red))
                    "menunggu konfirmasi" -> tvStatus.setTextColor(context.getColor(R.color.orange))
                    "berhasil" -> tvStatus.setTextColor(context.getColor(R.color.primary))
                    else -> tvStatus.setTextColor(context.getColor(R.color.grey))
                }

                root.setOnClickListener {
                    when(transaction.status.lowercase()) {
                        "pending" -> {
                            val intent = Intent(context, DetailPendingNabungActivity::class.java)
                            intent.putExtra("transaction", transaction)
                            context.startActivity(intent)
                        }
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryAdapter.HistoryViewHolder {
        return HistoryViewHolder(
            ItemTransactionHistoryBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: HistoryAdapter.HistoryViewHolder, position: Int) {
        val history = differ.currentList[position]
        holder.bind(history)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}