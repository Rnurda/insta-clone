import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.ryspay.nurda.R
import kotlinx.android.synthetic.main.dialog_password.view.*


class PasswordDialog : DialogFragment(){
    private lateinit var mListener: Listener

    interface Listener{
        fun onPasswordConfirm(password: String)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mListener = context as Listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_password, null)
        return AlertDialog.Builder(context!!)
            .setView(view)
            .setPositiveButton(android.R.string.ok){ dialog, id ->
                mListener.onPasswordConfirm(view.password_input.text.toString())
            }
            .setNegativeButton(android.R.string.cancel){ dialog, id ->{

            }}
            .setTitle(R.string.please_enter_password)
            .create()
    }
}