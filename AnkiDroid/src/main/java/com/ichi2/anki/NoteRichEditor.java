package com.ichi2.anki;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import com.ichi2.anim.ActivityTransitionAnimation;
import com.ichi2.async.DeckTask;
import com.ichi2.libanki.Card;
import com.ichi2.libanki.Note;
import jp.wasabeef.richeditor.RichEditor;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

import static com.ichi2.anki.NoteEditor.CALLER_NOCALLER;

/**
 * 富文本编辑
 */
public class NoteRichEditor extends AnkiActivity {

	public static final String PARAM_BACK = "back";
	public static final String PARAM_FRONT = "front";
	private EditText noteFront;
	private Card mCurrentEditedCard;
	private Note mEditorNote;
	private int mCaller;
	private boolean mAddNote;
	private String mFront;
	private String mBack;
	private boolean mBackChanged;
	private RichEditor mEditor;
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
	private View toolbarContainer;

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

		initRichEditor();

		if (mEditorNote != null) {
			mFront = mEditorNote.getItem("Front");
			noteFront.setText(mFront);
			mBack = mEditorNote.getItem("Back");
			mEditor.setHtml(mBack);
		}

	}

	private void initRichEditor() {

		mEditor = (RichEditor) findViewById(R.id.note_back);
//		mEditor.setEditorHeight(200);
//		mEditor.setEditorFontSize(22);
//		mEditor.setEditorFontColor(Color.RED);
		//mEditor.setEditorBackgroundColor(Color.BLUE);
		//mEditor.setBackgroundColor(Color.BLUE);
		//mEditor.setBackgroundResource(R.drawable.bg);
		mEditor.setPadding(10, 10, 10, 10);
		//mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
//		mEditor.setPlaceholder("");
		//mEditor.setInputEnabled(false);
		toolbarContainer = findViewById(R.id.rte_toolbar_container);
		mEditor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					toolbarContainer.setVisibility(View.VISIBLE);
				}else {
					toolbarContainer.setVisibility(View.GONE);
				}
			}
		});
		mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
			@Override public void onTextChange(String text) {
				mBackChanged = true;
			}
		});

		findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mEditor.undo();
			}
		});

		findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mEditor.redo();
			}
		});

		findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mEditor.setBold();
			}
		});

		findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mEditor.setItalic();
			}
		});

		findViewById(R.id.action_subscript).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mEditor.setSubscript();
			}
		});

		findViewById(R.id.action_superscript).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mEditor.setSuperscript();
			}
		});

		findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mEditor.setStrikeThrough();
			}
		});

		findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mEditor.setUnderline();
			}
		});

		findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mEditor.setHeading(1);
			}
		});

		findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mEditor.setHeading(2);
			}
		});

		findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mEditor.setHeading(3);
			}
		});

		findViewById(R.id.action_heading4).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mEditor.setHeading(4);
			}
		});

		findViewById(R.id.action_heading5).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mEditor.setHeading(5);
			}
		});

		findViewById(R.id.action_heading6).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mEditor.setHeading(6);
			}
		});

		findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
			private boolean isChanged;

			@Override public void onClick(View v) {
				mEditor.setTextColor(isChanged ? Color.BLACK : Color.RED);
				isChanged = !isChanged;
			}
		});

		findViewById(R.id.action_bg_color).setOnClickListener(new View.OnClickListener() {
			private boolean isChanged;

			@Override public void onClick(View v) {
				mEditor.setTextBackgroundColor(isChanged ? Color.TRANSPARENT : Color.YELLOW);
				isChanged = !isChanged;
			}
		});

		findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mEditor.setIndent();
			}
		});

		findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mEditor.setOutdent();
			}
		});

		findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mEditor.setAlignLeft();
			}
		});

		findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mEditor.setAlignCenter();
			}
		});

		findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mEditor.setAlignRight();
			}
		});

		findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mEditor.setBlockquote();
			}
		});

		findViewById(R.id.action_insert_bullets).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mEditor.setBullets();
			}
		});

		findViewById(R.id.action_insert_numbers).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mEditor.setNumbers();
			}
		});

		findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
//				mEditor.insertImage("http://www.1honeywan.com/dachshund/image/7.21/7.21_3_thumb.JPG",
//						"dachshund");
			}
		});

		findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
//				mEditor.insertLink("https://github.com/wasabeef", "wasabeef");
			}
		});
		findViewById(R.id.action_insert_checkbox).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mEditor.insertTodo();
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
				String html = mEditor.getHtml();
				if (TextUtils.isEmpty(front) && TextUtils.isEmpty(html)) {
					setResult(RESULT_CANCELED);
					finishWithAnimation(ActivityTransitionAnimation.NONE);
					return;
				}
				mEditorNote.values()[0] = front;
				mEditorNote.values()[1] = html;
				mEditorNote.model().put("did", CardBrowser.mRestrictOnDeckId);
				getCol().getModels().setChanged();
				DeckTask.launchDeckTask(DeckTask.TASK_TYPE_ADD_FACT, mAddNoteListener, new DeckTask.TaskData(mEditorNote));
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}else {
			String backHtml = mEditor.getHtml();
			String newFront = noteFront.getText().toString();
			boolean mChanged = !newFront.equals(mFront) || (mBackChanged && !backHtml.equals(mBack));
			if (mChanged) {
				if (!mEditorNote.values()[0].equals(newFront)) {
					mEditorNote.values()[0] = newFront;
				}
				if (!mEditorNote.values()[1].equals(backHtml)) {
					mEditorNote.values()[1] = backHtml;
				}
				if (mEditorNote.getTags().contains("xlnote")) {
					mEditorNote.addTag("marked");   // 标记需要同步
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
