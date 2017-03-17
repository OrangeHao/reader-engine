package org.geometerplus.android.fbreader.api.aidl;

import org.geometerplus.android.fbreader.api.aidl.ApiObject;

interface ApiInterface {
	ApiObject request(int method, in ApiObject[] parameters);
	List<ApiObject> requestList(int method, in ApiObject[] parameters);
	Map requestMap(int method, in ApiObject[] parameters);
}
