package regression.objectconstruction.testgeneration.example.graphcontruction;

import java.util.Date;

public class Article implements ArticleMeta, ContentSetable {
	private static final long serialVersionUID = 5892823865821665643L;

	private WikiBot bot;

	private SimpleArticle sa;

	private int reload = 0;

	private static final int TEXT_RELOAD = 2;

	private static final int REVISION_ID_RELOAD = 4;

	private static final int MINOR_EDIT_RELOAD = 8;

	private static final int EDITOR_RELOAD = 16;

	private static final int EDIT_SUM_RELOAD = 32;

	private static final int EDIT_DATE_RELOAD = 64;

	public String getRevisionId() {
		if (isReload(4)) {
			setReload(4);
			try {
				this.sa.setRevisionId(this.bot.readData(this.sa.getTitle()).getRevisionId());
			} catch (JwbfException e) {
				throw new RuntimeException(e);
			}
		}
		return this.sa.getRevisionId();
	}

	private boolean isReload(int reloadVar) {
		if (this.bot.hasCacheHandler())
			return true;
		return ((this.reload & reloadVar) == 0);
	}

	private void setReload(int reloadVar) {
		this.reload |= reloadVar;
	}

	@Override
	public String getEditSummary() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEditor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isMinorEdit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEditSummary(String paramString) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMinorEdit(boolean paramBoolean) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTitle(String paramString) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setText(String paramString) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addText(String paramString) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addTextnl(String paramString) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEditor(String paramString) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isRedirect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Date getEditTimestamp() {
		// TODO Auto-generated method stub
		return null;
	}
}
