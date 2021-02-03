package com.example.notesapp.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.example.notesapp.R
import com.example.notesapp.data.model.Note
import com.example.notesapp.databinding.ActivityNoteBinding
import com.example.notesapp.ui.base.BaseActivity
import com.example.notesapp.viewmodel.NoteViewModel
import java.util.*

private const val SAVE_DELAY = 2000L

class NoteActivity : BaseActivity<Note?, NoteViewState>() {

    companion object {
        const val EXTRA_NOTE = "NoteActivity.extra.NOTE"

        fun getStartIntent(context: Context, noteId: String?): Intent {
            val intent = Intent(context, NoteActivity::class.java)

            intent.putExtra(EXTRA_NOTE, noteId)
            return intent
        }
    }

    override val viewModel: NoteViewModel by lazy { ViewModelProvider(this).get(NoteViewModel::class.java) }
    override val layoutRes: Int = R.layout.activity_note
    override val ui: ActivityNoteBinding by lazy { ActivityNoteBinding.inflate(layoutInflater) }

    private var note: Note? = null
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

        ui.titleEt.addTextChangedListener(textChangeListener)
        ui.bodyEt.addTextChangedListener(textChangeListener)
    }

    private fun initView() {
        note?.run {
            ui.toolbar.setBackgroundColor(color.getColorInt(this@NoteActivity))
            ui.titleEt.setText(title)
            ui.bodyEt.setText(note)

            supportActionBar?.title = lastChanged.format()
        }

        ui.titleEt.addTextChangedListener(textChangeListener)
        ui.bodyEt.addTextChangedListener(textChangeListener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun createNewNote(): Note = Note(
        UUID.randomUUID().toString(),
        ui.titleEt.text.toString(),
        ui.bodyEt.text.toString()
    )

    private fun triggerSaveNote() {
        if (ui.titleEt.text == null || ui.titleEt.text!!.length < 3) return
        Handler(Looper.getMainLooper()).postDelayed({
            note = note?.copy(
                title = ui.titleEt.text.toString(),
                note = ui.bodyEt.text.toString(),
                lastChanged = Date()
            ) ?: createNewNote()

            if (note != null) viewModel.saveChanges(note!!)
        }, SAVE_DELAY)
    }

    override fun renderData(data: Note?) {
        this.note = data
        initView()
    }

}