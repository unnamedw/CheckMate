package com.example.msg_b.checkmate

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.msg_b.checkmate.MainActivity
import com.example.msg_b.checkmate.util.CurrentUserManager
import com.example.msg_b.checkmate.util.User
import com.facebook.AccessToken
import com.facebook.accountkit.AccountKit
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeResponseCallback
import com.kakao.usermgmt.response.model.UserProfile
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MainActivity : BaseActivity() {
    private val TAG_SETTING = 1003
    private var LOGIN_TYPE = 0
    private var LOGIN_CHECKER = 0
    private var login_id: String? = null
    private var login_type: String? = null
    var mTask: LoginTask? = null
    var longi: Double? = null
    var lati: Double? = null
    private val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 111
    var lm: LocationManager? = null

    companion object {
        const val ACCOUNT_KIT = 1000
        const val KAKAO = 1001
        const val FACEBOOK = 1002
    }

    /**
     * [MainActivity 작업 순서도]
     *
     * 1. onCreate 에서 현재 존재하고 있는 토큰을 확인한다. (AccountKit, Kakao, Facebook API)
     * 토큰이 존재하고 있는 경우 LOGIN_CHECKER 라는 변수를 1씩 더한다.
     * LOGIN_TYPE 에는 토큰이 존재하는 API 이름을 저장.
     *
     * 2. 다음은 앞서 저장된 변수들을 확인하는 과정이다.
     * LOGIN_CHECKER 가 1이 아닌 경우에는 토큰이 존재하지 않거나 중복토큰이 존재하는 것이므로
     * 모든 API 의 토큰과 세션을 초기화하고 다시 로그인을 요구한다.
     * LOGIN_CHECKER 가 1인 경우, 토큰이 정상적으로 존재하는 것이므로
     * 이 때는 login_id 와 login_type 에 각각 고유 id 와 API 유형을 저장하고 LoginTask 를 진행한다.
     *
     * 3. LoginTask 에서는 login_id 와 login_type 을 서버로 보내 DB 에서 확인작업을 진행한다.
     * 이때 정상적인 응답으로 돌아오는 값은 -1, 0, 1이다.
     * 응답이 -1인 경우, DB 상에 해당 유저의 아이디가 존재하지 않는 경우이므로 이 경우 DB 에 계정을 생성하고 필수절차를 진행한다.
     * 응답이 0인 경우, DB 상에 해당 유저의 아이디는 존재하나 필수정보가 없으므로 필수절차를 진행한다.
     * 응답이 1인 경우, 이미 정상적으로 등록된 회원이므로 해당 유저를 현재 로그인 유저로 저장한다.
     *
     * 4. UpdateCurrentUserTask 를 통해 SharedPreferences 에 현재 유저의 정보가 저장되며
     * 해당 작업이 끝나면 setTokenIdTask 를 진행한다.
     *
     * 5. setTokenIdTask 는 로그인 한 앱의 고유 토큰에 현재 로그인 된 아이디를 지정하는 Task 이며
     * 정상적으로 토큰이 세팅된 경우 GPS 활성화가 되어있는지 확인한다.
     * GPS 기능이 활성화 되어있지 않은 경우 GPS 설정 창으로 이동한다.
     * GPS 기능이 정상적으로 활성화 되어있는 경우 HomeActivity 로 이동한다.
     *
     *
     * 보류중. getLocation 은 현재 기기의 위치정보를 받아오는 메소드로서 위치를 받아온 후에
     * updateLocationTask 를 진행한다.
     *
     * 보류중. updateLocationTask 는 현재 로그인 된 유저에게 앞서 받아온 위치정보를 DB 에 반영하는 Task 이며
     * 이 과정이 완료되면 Home 화면으로 이동한다.
     *
     *
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        // 타이틀 바를 숨김
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        @SuppressLint("WrongThread") val mId = FirebaseInstanceId.getInstance().id
        val mToken = FirebaseInstanceId.getInstance().token
        if (mId != null && mToken != null) {
            Log.d("fcmtest", mId)
            Log.d("fcmtest", mToken)
        } else {
            Log.d("fcmtest", "null")
        }


        /** AccountKit 오류 해결을 위한 임시 코드
         * 앱을 지웠다 다시 설치한 경우 일단 모든 토큰을 없애고 다시 로그인을 요구한다. */
        val sf = getSharedPreferences("APP", Context.MODE_PRIVATE)
        val editor = sf.edit()
        if (sf.contains("status")) {
            // if app has already installed.
            Log.d("apptest", "already installed")
        } else {
            // if this is first open.
            editor.putString("status", "1")
            editor.commit()
            Log.d("apptest", "first run")
            CurrentUserManager.LogoutAll()
        }


        // 권한 요청
        requestPermission()
    } // onCreate

    fun loginCheck() {
        /** 로그인 체크  */
        LOGIN_CHECKER = 0
        LOGIN_TYPE = 0
        val userId = ""
        //AccountKit.initialize(getApplicationContext());
        val accessToken = AccountKit.getCurrentAccessToken()
        if (accessToken != null) {
            Log.d("loginT", "AccountKit Token exist")
            Log.d("loginT", accessToken.token)
            LOGIN_TYPE = ACCOUNT_KIT
            LOGIN_CHECKER = +1
            //userId = AccountKit.getCurrentAccessToken().getAccountId();
        } else {
            Log.d("loginT", "AccountKit Token doesn't exist")
        }
        if (Session.getCurrentSession().isOpened) {
            Log.d("loginT", "Kakao Session is opened")
            LOGIN_TYPE = KAKAO
            LOGIN_CHECKER = +1
        } else {
            Log.d("loginT", "Kakao Session is closed")
        }
        if (AccessToken.getCurrentAccessToken() != null) {
            Log.d("loginT", "Facebook Token exist")
            LOGIN_TYPE = FACEBOOK
            LOGIN_CHECKER = +1
            //userId = AccessToken.getCurrentAccessToken().getUserId();
        } else {
            Log.d("loginT", "Facebook Token doesn't exist")
        }
        //        CurrentUserManager.setCurrentUserId(this, userId);
//        Log.d("idtest", userId);
        /**
         * LOGIN_CHECKER 는 현재 유지되고 있는 AccessToken 이나 열려있는 Session 의 갯수를 의미한다.
         * LOGIN_CHECKER 가 1이 아니면 정상적인 로그인이 되지 않았다고 판단한다.
         * LOGIN_TYPE 은 현재 로그인된 유저가 어떤 방법으로 로그인했는지를 나타낸다.  */
        /** 토큰이 정상적으로 존재하는 경우(LOGIN_CHECKER == 1)  */
        if (LOGIN_CHECKER == 1) {
            Log.d("loginT", " Checker = 1")
            when (LOGIN_TYPE) {
                ACCOUNT_KIT -> {
                    login_id = AccountKit.getCurrentAccessToken()!!.accountId
                    login_type = "ACCOUNT_KIT"
                    mTask = LoginTask()
                    mTask!!.execute()
                }
                KAKAO -> KakaoUserLogin()
                FACEBOOK -> {
                    login_id = AccessToken.getCurrentAccessToken().userId
                    login_type = "FACEBOOK"
                    mTask = LoginTask()
                    mTask!!.execute()
                }
                else -> {
                }
            }
            Log.d("mTask", "login_id : $login_id\nlogin_type : $login_type")


            //기존에 로그인 한 토큰이 남아있는 경우 현재 저장되어 있는 아이디를 확인한다.
            //저장된 아이디가 있으면 계정이 존재하므로 해당 아이디만 파라미터로 보낸다.
            //저장된 아이디도 없는 경우 꼬인 상태이므로 아이디와 타입 모두 빈 값을 파라미터로 보낸다.
            //이후 태스크에서 어차피 오류값을 반환할 것이므로 다시 로그인하는 과정으로 돌아가게 된다.
            /** 회원정보 확인을 위한 login_id, login_type 을 세팅하는 부분
             * LoginActivity 로 부터 전달받은 값이 있으면 저장하고
             * 없으면 현재 저장되어 있는 아이디 값을 넣는다.
             * 나머지는 #default 처리  */
        } else {
            Log.d("loginT", "Checker != 1")
            Toast.makeText(this, "정상적인 토큰이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
            CurrentUserManager.LogoutAll()
            CurrentUserManager.initCurrentUser(this)
            goToLoginActivityAndFinish()
        }
    }

    /** 현재 돌고있는 AsyncTask 가 있다면 Activity 종료시에 같이 종료시킨다.  */
    override fun onDestroy() {
        Log.d("mTask", "Main onDestroy")
        try {
            if (mTask!!.status == AsyncTask.Status.RUNNING) {
                mTask!!.cancel(true)
            } else {
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("mTask", "Main onActivityResult")
        if (requestCode == TAG_SETTING) {
            if (lm!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                goToHomeActivityAndFinish()
            } else {
                goToActivateGPS()
            }
        }
    }

    /** Activity 전환  */
    fun goToLoginActivityAndFinish() {
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    //로그인 화면으로 이동
    fun goToHomeActivityAndFinish() {
        val intent = Intent(this@MainActivity, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    //홈 화면으로 이동
    fun goToRegisterActivityAndFinish() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }
    //가입절차 화면으로 이동
    /** 회원정보 확인을 위한 Task  */
    inner class LoginTask : AsyncTask<String?, Void?, String?>() {
        var mDialog = ProgressDialog(this@MainActivity)

        // 로딩화면을 위한 다이얼로그 선언
        override fun onPreExecute() {


            //mainDialog.show();
            // 로딩화면 띄우기
        }

        override fun doInBackground(vararg p0: String?): String? {
            val client = OkHttpClient()
            val strUrl = "http://115.71.238.160/novaproject1/MainActivity/user_create.php"
            var result: String? = null
            val body: RequestBody = FormBody.Builder()
                    .add("id", login_id!!) //현재 로그인 된 유저의 id를 'id'라는 키값으로 보냄
                    .add("type", login_type!!)
                    .build()
            val request = Request.Builder()
                    .url(strUrl)
                    .post(body)
                    .build()
            try {
                val response = client.newCall(request).execute()
                result = response.body!!.string()
                // 서버에 id 라는 키 값으로 현재 아이디(CurrentUserId)를 보낸다.
                // 이때 아이디가 존재하면 1을, 존재하지 않으면 -1을 결과값으로 리턴받는다.
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(s: String?) {
            Log.d("mTask", s)
            //mDialog.dismiss();
            // 로딩화면 제거
            if (s == "1") {
                Toast.makeText(this@MainActivity, "이미 가입된 회원입니다.", Toast.LENGTH_SHORT).show()
                //requestPermission();
                //goToHomeActivityAndFinish();
                UpdateCurrentUserTask(login_id!!).execute()
                //이미 가입된 회원이면 홈화면으로 이동
            } else if (s == "0") {
                Toast.makeText(this@MainActivity, "가입절차가 완료되지 않았습니다. \n 필수정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
                goToRegisterActivityAndFinish()
                //필수정보를 입력하지 않은 회원의 경우 입력화면으로 이동
            } else if (s == "-1") {
                if (login_id == "") {
                    CurrentUserManager.initCurrentUser(this@MainActivity)
                    CurrentUserManager.LogoutAll()
                    goToLoginActivityAndFinish()
                    //위에서 언급한 꼬인 상황이므로 로그인 절차를 다시 진행한다.
                } else {
                    Toast.makeText(this@MainActivity, "신규 회원으로 등록합니다.", Toast.LENGTH_SHORT).show()
                    goToRegisterActivityAndFinish()
                    //신규 회원이면 가입화면으로 이동
                }
            } else {
                Toast.makeText(this@MainActivity, "유저정보 확인 실패", Toast.LENGTH_SHORT).show()
                CurrentUserManager.LogoutAll()
                CurrentUserManager.setCurrentUserId(this@MainActivity, null)


                //예외의 경우 로그아웃 처리 후 로그인 화면으로 이동
            }
            //mainDialog.hide();
        }
    } //LoginTask

    /** Permission 여부를 확인하고 요청한다.  */
    fun requestPermission() {
        Log.d("permissionT", "요청실행")
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("permissionT", "권한이 없네?")
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d("permissionT", "설명이 좀 필요해")
                val adb = AlertDialog.Builder(this@MainActivity)
                adb.setMessage("서비스 이용을 위해 위치정보가 필요합니다.")
                adb.setCancelable(false)

                //Positive[오른쪽]
                adb.setPositiveButton("확인") { dialog, which ->
                    ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
                }
                adb.show()

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                Log.d("permissionT", "설명 안해도 돼")
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            Log.d("permissionT", "권한 이미 있어")
            loginCheck()
        }
    }

    /** Permission 콜백  */
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permissionT", "권한 승인했음")
                    Toast.makeText(this@MainActivity, "권한이 승인되었습니다.", Toast.LENGTH_SHORT).show()
                    loginCheck()
                    //new UpdateCurrentUserTask(login_id).execute();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Log.d("permissionT", "권한 거절함")
                    Toast.makeText(this, "권한 요청이 거절되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

    private fun KakaoUserLogin() {
        UserManagement.requestMe(object : MeResponseCallback() {
            override fun onSessionClosed(errorResult: ErrorResult) {}
            override fun onNotSignedUp() {}
            override fun onSuccess(result: UserProfile) {
                Log.d("idtest", "id : " + java.lang.Long.toString(result.id))
                Log.d("idtest", result.profileImagePath)
                Log.d("idtest", result.thumbnailImagePath)
                login_id = result.id.toString()
                login_type = "KAKAO"
                LoginTask().execute()
            }
        })
    }

    private inner class UpdateCurrentUserTask(var mId: String) : AsyncTask<String?, Void?, User?>() {
        override fun onPreExecute() {
            super.onPreExecute()
            Log.d("permissionT", "preUpdating")
        }

        override fun onPostExecute(user: User?) {
            super.onPostExecute(user)
            Log.d("permissionT", "postUpdating")
            CurrentUserManager.setCurrentUser(applicationContext, user)
            val paramsId = CurrentUserManager.getCurrentUserId(applicationContext)
            if (FirebaseInstanceId.getInstance().token != null) {
                val paramsToken = FirebaseInstanceId.getInstance().token
                setTokenIdTask().execute(paramsId, paramsToken)
            } else {
                Toast.makeText(this@MainActivity, "토큰이 제대로 생성되지 않았습니다. 앱을 재실행하여 주세요.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        override fun doInBackground(vararg p0: String?): User? {
            Log.d("permissionT", "backgroundUpdating")
            val strUrl = "http://115.71.238.160/novaproject1/HomeActivity/ProfileFragment/ProfileActivity/getuser.php"
            var result: User? = null
            val requestBody: RequestBody = FormBody.Builder()
                    .add("id", mId)
                    .build()
            val request = Request.Builder()
                    .url(strUrl)
                    .post(requestBody)
                    .build()
            val mClient = OkHttpClient()
            val mCall = mClient.newCall(request)
            try {
                val mResponse = mCall.execute()
                val data = mResponse.body!!.string()
                Log.d("chatTT", data)
                val mUser = Gson().fromJson(data, User::class.java)
                result = mUser
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return result
        }

    }

    /**
     * 현재 앱의 fcm token 에 로그인 된 id를 세팅한다.
     * 앱의 진입점인 MainActivity 와 앱 설치시 토큰을 생성하는 fcm Instance 클래스에 onRefreshToken 에 넣어준다. */
    internal inner class setTokenIdTask : AsyncTask<String?, Void?, String?>() {
        override fun doInBackground(vararg p0: String?): String? {
            val strUrl = "http://115.71.238.160/novaproject1/fcmid.php"
            var result: String? = null

            //파라미터의 첫 번째 변수는 현재의 id, 두 번째 변수는 앱의 fcm 토큰이다.
            val requestBody: RequestBody = FormBody.Builder()
                    .add("id", p0[0] ?: "")
                    .add("token", p0[1] ?: "")
                    .build()
            val request = Request.Builder()
                    .url(strUrl)
                    .post(requestBody)
                    .build()
            val mClient = OkHttpClient()
            val mCall = mClient.newCall(request)
            try {
                val mResponse = mCall.execute()
                result = mResponse.body!!.string()
                Log.d("fcmT", "result : $result")
                Log.d("fcmT", p0[0] ?: "")
                Log.d("fcmT", p0[1] ?: "")
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(s: String?) {
            super.onPostExecute(s)
            if (s == "1") {
                Toast.makeText(this@MainActivity, "토큰 id 업데이트 성공\n결과값 : $s", Toast.LENGTH_SHORT).show()
            } else if (s == "0") {
                Toast.makeText(this@MainActivity, "토큰 id 업데이트 실패\n결과값 : $s", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "토큰 존재 x\n결과값 : $s", Toast.LENGTH_SHORT).show()
            }
            if (lm!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                goToHomeActivityAndFinish()
                Log.d("T2", "GPS is enabled")
            } else {
                goToActivateGPS()
                Log.d("T2", "GPS is disabled")
            }
        }
    }

    /** GPS 동의를 물어보고 활성화시키는 기능  */
    fun goToActivateGPS() {
        val adb = AlertDialog.Builder(this@MainActivity)
        adb.setMessage("서비스 이용을 위해 GPS 활성화가 필요합니다.")
        adb.setCancelable(false)

        //Negative[왼쪽]
        adb.setNegativeButton("동의안함") { dialog, which -> finish() }

        //Positive[오른쪽]
        adb.setPositiveButton("동의") { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            startActivityForResult(intent, TAG_SETTING)
        }
        adb.show()
    }

    /** 위치정보를 업데이트하는 Task  */
    internal inner class updateLocationTask : AsyncTask<String?, Void?, String?>() {
        var _id: String? = null
        var _status: String? = null
        var _longi: String? = null
        var _lati: String? = null
        override fun doInBackground(vararg p0: String?): String? {
            _id = p0[0] ?: ""
            _status = p0[1] ?: ""
            _longi = p0[2] ?: ""
            _lati = p0[3] ?: ""
            Log.d("locT", "doing updateLocationTask")
            var result: String? = null
            try {
                val url = URL("http://115.71.238.160/novaproject1/setlocation.php")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST" // URL 요청에 대한 메소드 설정 : POST.
                conn.setRequestProperty("Accept-Charset", "UTF-8") // Accept-Charset 설정.
                conn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8")
                val outData = "id=$_id&status=$_status&longi=$_longi&lati=$_lati"
                val outputStream = conn.outputStream
                outputStream.write(outData.toByteArray(charset("UTF-8")))
                outputStream.flush()
                outputStream.close()
                /** 응답 받기  */
                if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                    Log.d("thTT", conn.responseMessage)
                }
                val bufferedReader = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
                var line: String?
                val page = StringBuilder()

                // 라인을 받아와 합친다.
                while (bufferedReader.readLine().also { line = it } != null) {
                    page.append(line)
                }
                result = page.toString()
                Log.d("thTT", page.toString())
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(s: String?) {
            super.onPostExecute(s)
            Log.d("locT", "post updateLocationTask")
            if (s == "1") {
                Toast.makeText(applicationContext, "위치정보 저장 성공", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext, "에러코드 : $s", Toast.LENGTH_SHORT).show()
            }
            goToHomeActivityAndFinish()
        }
    }// 등록할 위치제공자
    // 통지사이의 최소 변경거리 (m)
// 등록할 위치제공자
    // 통지사이의 최소 변경거리 (m)
// 변경시// Enabled시// Disabled시//위치제공자
    //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
    //Network 위치제공자에 의한 위치변화
    //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
    //                Toast.makeText(HomeActivity.this, result, Toast.LENGTH_SHORT).show();
//정확도//고도//위도//경도//여기서 위치값이 갱신되면 이벤트가 발생한다.
    //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

    //        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    // GPS 제공자의 정보가 바뀌면 콜백하도록 리스너 등록하기~!!!
    val location: Unit
        get() {
//        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // GPS 제공자의 정보가 바뀌면 콜백하도록 리스너 등록하기~!!!
            val mLocationListener: LocationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    //여기서 위치값이 갱신되면 이벤트가 발생한다.
                    //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.
                    Log.d("locT", "onLocationChanged, location:$location")
                    val longitude = location.longitude //경도
                    val latitude = location.latitude //위도
                    val altitude = location.altitude //고도
                    val accuracy = location.accuracy //정확도
                    val provider = location.provider //위치제공자
                    //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
                    //Network 위치제공자에 의한 위치변화
                    //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
                    val result = """
                        위치정보 : $provider
                        위도 : $longitude
                        경도 : $latitude
                        고도 : $altitude
                        정확도 : $accuracy
                        """.trimIndent()
                    Log.d("locT", result)
                    //                Toast.makeText(HomeActivity.this, result, Toast.LENGTH_SHORT).show();
                    longi = location.longitude
                    lati = location.latitude
                    val id = CurrentUserManager.getCurrentUserId(applicationContext)
                    val status = "1"
                    updateLocationTask().execute(id, status, longi.toString() + "", lati.toString() + "")
                }

                override fun onProviderDisabled(provider: String) {
                    // Disabled시
                    Log.d("locT", "onProviderDisabled, provider:$provider")
                }

                override fun onProviderEnabled(provider: String) {
                    // Enabled시
                    Log.d("locT", "onProviderEnabled, provider:$provider")
                }

                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
                    // 변경시
                    Log.d("locT", "onStatusChanged, provider:$provider, status:$status ,Bundle:$extras")
                }
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED) {
                Log.d("locT", "권한있음")
                lm!!.requestLocationUpdates(LocationManager.GPS_PROVIDER,  // 등록할 위치제공자
                        600000, 1f,  // 통지사이의 최소 변경거리 (m)
                        mLocationListener)
                lm!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,  // 등록할 위치제공자
                        600000, 1f,  // 통지사이의 최소 변경거리 (m)
                        mLocationListener)
            } else {
                Log.d("locT", "권한없음")
            }
        }


}