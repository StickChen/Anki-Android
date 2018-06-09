package com.ichi2.anki;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.widget.EditText;
import com.ichi2.anim.ActivityTransitionAnimation;
import com.ichi2.async.DeckTask;
import com.ichi2.libanki.Card;
import com.ichi2.libanki.Note;
import com.onegravity.rteditor.RTEditText;
import com.onegravity.rteditor.RTManager;
import com.onegravity.rteditor.RTToolbar;
import com.onegravity.rteditor.api.RTApi;
import com.onegravity.rteditor.api.RTMediaFactoryImpl;
import com.onegravity.rteditor.api.RTProxyImpl;
import com.onegravity.rteditor.api.format.RTFormat;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

import static com.ichi2.anki.NoteEditor.CALLER_NOCALLER;

/**
 * 富文本编辑
 */
public class NoteRichEditor extends AnkiActivity {

	private RTEditText rtEditText;
	public static final String PARAM_BACK = "back";
	public static final String PARAM_FRONT = "front";
	private EditText noteFront;
	private Card mCurrentEditedCard;
	private Note mEditorNote;
	private int mCaller;
	private boolean mAddNote;
	private String mFront;
	private String mBack;
	private boolean backChanged;
	private DeckTask.Listener mAddNoteListener = new DeckTask.Listener() {
		@Override public void onPreExecute(DeckTask task) {

		}

		@Override public void onPostExecute(DeckTask task, DeckTask.TaskData result) {
			UIUtils.showThemedToast(NoteRichEditor.this, "添加成功", true);
			setResult(RESULT_OK);
			finishWithAnimation(ActivityTransitionAnimation.NONE);

		}

		@Override public void onProgressUpdate(DeckTask task, DeckTask.TaskData... values) {

		}

		@Override public void onCancelled() {

		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		if (savedInstanceState == null) {
			Intent intent = getIntent();
			mCaller = intent.getIntExtra(NoteEditor.EXTRA_CALLER, CALLER_NOCALLER);
		}
		switch (mCaller) {
		case CALLER_NOCALLER:
			return;
		case NoteEditor.CALLER_CARDBROWSER_ADD:
			mAddNote = true;
			break;
		case NoteEditor.CALLER_CARDBROWSER_EDIT:
			mCurrentEditedCard = CardBrowser.sCardBrowserCard;
			if (mCurrentEditedCard == null) {
				finishWithoutAnimation();
				return;
			}
			mEditorNote = mCurrentEditedCard.note();
			mAddNote = false;
			break;
		case NoteEditor.CALLER_REVIEWER:
			mCurrentEditedCard = AbstractFlashcardViewer.getEditorCard();
			if (mCurrentEditedCard == null) {
				finishWithoutAnimation();
				return;
			}
			mEditorNote = mCurrentEditedCard.note();
			mAddNote = false;
			break;
		}

		super.onCreate(savedInstanceState);
		setTheme(R.style.NRE_ThemeLight);
		setContentView(R.layout.note_rich_editor);

		// set subject
		noteFront = findViewById(R.id.note_front);
		if (mEditorNote != null) {
			mFront = mEditorNote.getItem("Front");
			noteFront.setText(mFront);
		}

		// create RTManager
		RTApi rtApi = new RTApi(this, new RTProxyImpl(this), new RTMediaFactoryImpl(this, true));
		RTManager rtManager = new RTManager(rtApi, savedInstanceState);

		// register toolbar
		ViewGroup toolbarContainer = (ViewGroup) findViewById(R.id.rte_toolbar_container);
		RTToolbar rtToolbar = (RTToolbar) findViewById(R.id.rte_toolbar);
		if (rtToolbar != null) {
			rtManager.registerToolbar(toolbarContainer, rtToolbar);
		}

		// register editor & set text
		rtEditText = (RTEditText) findViewById(R.id.note_back);
		rtManager.registerEditor(rtEditText, true);
		if (mEditorNote != null) {
			mBack = mEditorNote.getItem("Back");
			rtEditText.setRichTextEditing(true, mBack);
		}

		rtEditText.addTextChangedListener(new TextWatcher() {
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override public void afterTextChanged(Editable s) {
				backChanged = true;
			}
		});
	}

	@Override
	public void onBackPressed() {
		Timber.i("NoteRichEditor:: onBackPressed()");
		int result;
		// 当标题或者内容不一致的时候，就更新
		if (mAddNote) {
			JSONObject model = getCol().getModels().current();
			mEditorNote = new Note(getCol(), model);
			try {
				String front = noteFront.getText().toString();
				if (TextUtils.isEmpty(front)) {
					setResult(RESULT_CANCELED);
					finishWithAnimation(ActivityTransitionAnimation.NONE);
				}
				mEditorNote.values()[0] = front;
				mEditorNote.values()[1] = rtEditText.getText(RTFormat.HTML);
				mEditorNote.model().put("did", CardBrowser.mRestrictOnDeckId);
				getCol().getModels().setChanged();
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}

			DeckTask.launchDeckTask(DeckTask.TASK_TYPE_ADD_FACT, mAddNoteListener, new DeckTask.TaskData(mEditorNote));
		}else {
			String backHtml = rtEditText.getText(RTFormat.HTML);
			String newFront = noteFront.getText().toString();
			boolean mChanged = !newFront.equals(mFront) || (backChanged && !backHtml.equals(mBack));
			if (mChanged) {
				if (!mEditorNote.values()[0].equals(newFront)) {
					mEditorNote.values()[0] = newFront;
				}
				if (!mEditorNote.values()[1].equals(backHtml)) {
					mEditorNote.values()[1] = backHtml;
				}

				result = RESULT_OK;
			} else {
				result = RESULT_CANCELED;
			}
			setResult(result);
			finishWithAnimation(ActivityTransitionAnimation.NONE);
		}

	}
}
