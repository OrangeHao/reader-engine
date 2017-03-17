/*
 * Copyright (C) 2007-2013 Geometer Plus <contact@geometerplus.com>
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

package org.geometerplus.fbreader;

import android.os.Environment;

import org.geometerplus.zlibrary.core.options.ZLStringListOption;

import java.util.List;

public abstract class Paths {
	public static String cardDirectory() {
		return Environment.getExternalStorageDirectory().getPath();
	}

	private static String defaultBookDirectory() {
		return cardDirectory() + "/Books";
	}

	public static ZLStringListOption BookPathOption() {
		return new ZLStringListOption("Files", "BooksDirectory", defaultBookDirectory(), "\n");
	}

	public static ZLStringListOption FontPathOption() {
		return new ZLStringListOption("Files", "FontsDirectory", cardDirectory() + "/Fonts", "\n");
	}

	public static ZLStringListOption WallpaperPathOption() {
		return new ZLStringListOption("Files", "WallpapersDirectory", cardDirectory() + "/Wallpapers", "\n");
	}

	public static String mainBookDirectory() {
		final List<String> bookPath = BookPathOption().getValue();
		return bookPath.isEmpty() ? defaultBookDirectory() : bookPath.get(0);
	}

	public static String cacheDirectory() {
		return mainBookDirectory() + "/.FBReader";
	}

	public static String networkCacheDirectory() {
		return cacheDirectory() + "/cache";
	}

	public static String systemShareDirectory() {
		return "/system/usr/share/FBReader";
	}
}
