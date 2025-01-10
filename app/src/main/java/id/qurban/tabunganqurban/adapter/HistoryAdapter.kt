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
import id.qurban.tabunganqurban.ui.detail.berhasil.DetailBerhasilNabungActivity
import id.qurban.tabunganqurban.ui.detail.mengecek.DetailMengecekNabungActivity
import id.qurban.tabunganqurban.ui.detail.waiting.DetailWaitingNabungActivity
import id.qurban.tabunganqurban.utils.FormatHelper.formatCurrencyDouble

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

    private var onItemClickListener: ((Transaction) -> Unit)? = null

    fun setOnItemClickListener(listener: (Transaction) -> Unit) {
        onItemClickListener = listener
    }

    inner class HistoryViewHolder(private val binding: ItemTransactionHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: Transaction) {
            binding.apply {
                tvIdTransaksi.text = context.getString(R.string.id_transaction, transaction.transactionId.takeLast(10))
                tvNominalTransaksi.text = formatCurrencyDouble(transaction.amount) // Format nominal
                tvStatus.text = transaction.status
                tvDateTransaksi.text = transaction.dateCreated // Tanggal sesuai format WIB
                // Tambahkan style warna status jika diperlukan
                when (transaction.status.lowercase()) {
                    "menunggu konfirmasi" -> tvStatus.setTextColor(context.getColor(R.color.red))
                    "mengecek" -> tvStatus.setTextColor(context.getColor(R.color.orange))
                    "berhasil" -> tvStatus.setTextColor(context.getColor(R.color.primary))
                    else -> tvStatus.setTextColor(context.getColor(R.color.grey))
                }

                root.setOnClickListener {
                    onItemClickListener?.invoke(transaction)
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