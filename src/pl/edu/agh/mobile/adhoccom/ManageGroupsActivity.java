package pl.edu.agh.mobile.adhoccom;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;

public class ManageGroupsActivity extends ListActivity {
	public static final String SELECTED_CHAT_GROUP = "chat_group";
	public static final String CHAT_SERVICE = "chat_service";

	private static final int CHANGE_GROUP_ID = Menu.FIRST;
	private static final int LEAVE_ID = Menu.FIRST + 1;
	
	private ChatService mChatService = null;
	private boolean mServiceBound = false;
	private ArrayAdapter<String> mAdapter = null;

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mChatService = ((ChatService.ChatServiceBinder) service)
					.getService();
			initializeFields();
		}

		public void onServiceDisconnected(ComponentName className) {
			mChatService = null;
			mServiceBound = false;
		}
	};

	@Override
	protected void onStop() {
		super.onStop();
		if (mServiceBound) {
			unbindService(mConnection);
			mServiceBound = false;
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if (!mServiceBound) {
			Intent bindIntent = new Intent(this, ChatService.class);
			bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);
			mServiceBound = true;
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manage_groups_layout);
		registerForContextMenu(getListView());
	}

	private void initializeFields() {
		if (mChatService != null) {
			mAdapter = new ArrayAdapter<String>(this, R.layout.groups_row,
					mChatService.getChatGroups());
			setListAdapter(mAdapter);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, CHANGE_GROUP_ID, 0, R.string.change_group);
		menu.add(0, LEAVE_ID, 1, R.string.menu_leave_gorup);
	}

	private void changeGroup(String groupName) {
		Intent retVal = new Intent();
		retVal.putExtra(SELECTED_CHAT_GROUP, groupName);
		setResult(RESULT_OK, retVal);
		finish();
	}

	private void leaveGroup(final String groupName) {
		String question = getString(R.string.group_leave_question) + ": "
				+ groupName;
		Utils.createYesNoDialog(this, question,
				new Utils.YesNoDialogListener() {
					@Override
					public void onYes() {
						try {
							mChatService.leaveGroup(groupName);
							mAdapter.remove(groupName);
						} catch (ChatServiceException ex) {
							Utils.createMessageDialog(
									ManageGroupsActivity.this, ex).show();
						}
					}

					@Override
					public void onNo() {
					}
				}).show();
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		final String groupName = mAdapter.getItem(info.position);

		switch (item.getItemId()) {
		case CHANGE_GROUP_ID:
			changeGroup(groupName);
			break;
		case LEAVE_ID:
			leaveGroup(groupName);
			return true;
		}
		return super.onContextItemSelected(item);
	}
}
