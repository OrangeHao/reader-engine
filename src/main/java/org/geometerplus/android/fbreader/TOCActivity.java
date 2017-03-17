/*
 * Copyright (C) 2009-2013 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.geometerplus.android.util.ViewUtil;
import org.geometerplus.fbreader.bookmodel.TOCTree;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.core.resources.ZLResource;
import org.geometerplus.zlibrary.core.tree.ZLTree;
import org.geometerplus.zlibrary.text.view.ZLTextWordCursor;

import org.R;

public class TOCActivity extends Activity
{
	private TOCAdapter myAdapter;
	private ZLTree<?> mySelectedItem;

	ListView lv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toc);
		init();
	}

	public void init( )
	{
		Log.d("czh","TOCActivity init");
		lv=(ListView) findViewById(R.id.lv);

		Thread.setDefaultUncaughtExceptionHandler(new org.geometerplus.zlibrary.ui.android.library.UncaughtExceptionHandler(this));
		final FBReaderApp fbreader = (FBReaderApp) ZLApplication.Instance();
		final TOCTree root = fbreader.Model.TOCTree;
		myAdapter = new TOCAdapter(root);
		final ZLTextWordCursor cursor = fbreader.BookTextView.getStartCursor();
		int index = cursor.getParagraphIndex();
		if (cursor.isEndOfParagraph())
		{
			++index;
		}

		TOCTree treeToSelect = fbreader.getCurrentTOCElement();
		myAdapter.selectItem(treeToSelect);
		mySelectedItem = treeToSelect;
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		OrientationUtil.setOrientation(this, getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		OrientationUtil.setOrientation(this, intent);
	}

	private static final int PROCESS_TREE_ITEM_ID = 0;
	private static final int READ_BOOK_ITEM_ID = 1;

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		final int position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
		final TOCTree tree = (TOCTree) myAdapter.getItem(position);
		switch (item.getItemId())
		{
		case PROCESS_TREE_ITEM_ID:
			myAdapter.runTreeItem(tree);
			return true;
		case READ_BOOK_ITEM_ID:
			myAdapter.openBookText(tree);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private final class TOCAdapter extends ZLTreeAdapter
	{
		TOCAdapter(TOCTree root)
		{
			super(lv, root);
		}

		@Override
		public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo)
		{
			final int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
			final TOCTree tree = (TOCTree) getItem(position);
			if (tree.hasChildren())
			{
				menu.setHeaderTitle(tree.getText());
				final ZLResource resource = ZLResource.resource("tocView");
				menu.add(0, PROCESS_TREE_ITEM_ID, 0, resource.getResource(isOpen(tree) ? "collapseTree" : "expandTree").getValue());
				menu.add(0, READ_BOOK_ITEM_ID, 0, resource.getResource("readText").getValue());
			}

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final View view = (convertView != null) ? convertView : LayoutInflater.from(parent.getContext()).inflate(
					R.layout.lv_toc, parent, false);
			final TOCTree tree = (TOCTree) getItem(position);
			ViewUtil.findTextView(view, R.id.lv_tv).setText(tree.getText());
			return view;
		}

		void openBookText(TOCTree tree)
		{
			final TOCTree.Reference reference = tree.getReference();
			if (reference != null)
			{
				finish();
				final FBReaderApp fbreader = (FBReaderApp) ZLApplication.Instance();
				fbreader.addInvisibleBookmark();
				fbreader.BookTextView.gotoPosition(reference.ParagraphIndex, 0, 0);
				fbreader.showBookTextView();
			}
		}
		@Override
		protected boolean runTreeItem(ZLTree<?> tree)
		{
			if (super.runTreeItem(tree)) { return true; }
			openBookText((TOCTree) tree);
			return true;
		}
	}


	@Override
	public void finish()
	{
		super.finish();

		overridePendingTransition(R.anim.keep_x,R.anim.activity_bottom_out);
	}
}
