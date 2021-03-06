package com.example.notesapp.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.example.notesapp.R
import com.example.notesapp.data.model.Color
import com.example.notesapp.data.model.Note
import com.example.notesapp.databinding.ActivityNoteBinding
import com.example.notesapp.ui.base.BaseActivity
import com.example.notesapp.viewmodel.NoteViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

private const val SAVE_DELAY = 2000L

class NoteActivity : BaseActivity<NoteViewState.Data>() {

    companion object {
        const val EXTRA_NOTE = "NoteActivity.extra.NOTE"

        fun getStartIntent(context: Context, noteId: String?): Intent {
            val intent = Intent(context, NoteActivity::class.java)

            intent.putExtra(EXTRA_NOTE, noteId)
            return intent
        }
    }

    override val viewModel: NoteViewModel by viewModel()
    override val layoutRes: Int = R.layout.activity_note
    override val ui: ActivityNoteBinding by lazy { ActivityNoteBinding.inflate(layoutInflater) }

    private var note: Note? = null
    private var color: Color = Color.RED
    private val textChangeListener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            triggerSaveNote()
        }

        override fun beforeTextChanged(
            s: CharSequence?, start: Int, count: Int, after: Int
        ) {// not used
        }

        override fun onTextChanged(
            s: CharSequence?, start: Int, before: Int, count: Int
        ) {// not used
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(ui.toolbar)

        val noteId = intent.getStringExtra(EXTRA_NOTE)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        noteId?.let {
            viewModel.loadNote(it)
        } ?: run {
            supportActionBar?.title = getString(R.string.new_note_title)
        }

        ui.colorPicker.onColorClickListener = {
            color = it
            setToolbarColor(it)
            triggerSaveNote()
        }

        setEditListener()
    }

    private fun initView() {
        note?.run {
            removeEditListener()
            if (title != ui.titleEt.text.toString()) {
                ui.titleEt.setText(title)
            }
            if (note != ui.bodyEt.text.toString()) {
                ui.bodyEt.setText(note)
            }
            setEditListener()

            supportActionBar?.title = lastChanged.format()

            setToolbarColor(color)
        }

    }

    private fun setToolbarColor(color: Color) {
        ui.toolbar.setBackgroundColor(color.getColorInt(this@NoteActivity))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean =
        menuInflater.inflate(R.menu.note_menu, menu).let { true }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> super.onBackPressed().let { true }
        R.id.palette -> togglePalette().let { true }
        R.id.delete -> deleteNote().let { true }
        else -> super.onOptionsItemSelected(item)
    }

    private fun togglePalette() {
        if (ui.colorPicker.isOpen) {
            ui.colorPicker.close()
        } else {
            ui.colorPicker.open()
        }
    }

    private fun deleteNote() {
        AlertDialog.Builder(this)
            .setMessage(R.string.delete_dialog_message)
            .setNegativeButton(R.string.cancel_btn_title) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.ok_bth_title) { _, _ -> viewModel.deleteNote() }
            .show()
    }


    private fun createNewNote(): Note = Note(
        UUID.randomUUID().toString(),
        ui.titleEt.text.toString(),
        ui.bodyEt.text.toString()
    )

    private fun triggerSaveNote() {
        if (ui.titleEt.text == null || ui.titleEt.text!!.length < 3) return

        launch {
            delay(SAVE_DELAY)
            note = note?.copy(
                title = ui.titleEt.text.toString(),
                note = ui.bodyEt.text.toString(),
                color = color,
                lastChanged = Date()
            ) ?: createNewNote()

            note?.let { viewModel.saveChanges(it) }
        }
    }

    override fun renderData(data: NoteViewState.Data) {
        if (data.isDeleted) finish()
        this.note = data.note
        data.note?.let { color = it.color }
        initView()
    }

    private fun setEditListener() {
        ui.titleEt.addTextChangedListener(textChangeListener)
        ui.bodyEt.addTextChangedListener(textChangeListener)
    }

    private fun removeEditListener() {
        ui.titleEt.removeTextChangedListener(textChangeListener)
        ui.bodyEt.removeTextChangedListener(textChangeListener)
    }

    override fun onBackPressed() {
        if (ui.colorPicker.isOpen) {
            ui.colorPicker.close()
            return
        }
        super.onBackPressed()
    }
}

