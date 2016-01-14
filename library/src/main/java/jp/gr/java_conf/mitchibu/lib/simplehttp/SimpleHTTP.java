package jp.gr.java_conf.mitchibu.lib.simplehttp;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import jp.gr.java_conf.mitchibu.lib.simplehttp.entity.Entity;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;

public class SimpleHTTP {
	private static final ExecutorService defaultExecutorService = Executors.newFixedThreadPool(5);

	private final Handler handler;
	private final ExecutorService executorService;

	/**
	 * コンストラクタ
	 */
	public SimpleHTTP(Context context) {
		this(context, defaultExecutorService);
	}

	/**
	 * コンストラクタ
	 * @param executorService undocumented
	 */
	public SimpleHTTP(Context context, ExecutorService executorService) {
		this.executorService = executorService;
		handler = new Handler(context.getMainLooper());
	}

	/**
	 * 非同期でHTTP通信を開始する。
	 * @param req リクエスト
	 * @param listener リスナ
	 * @return フューチャー
	 */
	public <E> Future<Response<E>> exec(Request<E> req, Listener<E> listener) {
		TaskExecutor<E> task = new TaskExecutor<E>(listener, createTask(req));
		executorService.submit(task);
		return task;
	}

	/**
	 * 同期でHTTP通信を開始する
	 * @param req リクエスト
	 * @return 結果
	 */
	public <E> Response<E> exec(Request<E> req) throws Exception {
		return createTask(req).call();
	}

	public void release() {
		executorService.shutdown();
	}

	protected <E> Task<E> createTask(Request<E> req) {
		return new HttpURLConnectionTask<E>(req);
	}

	public static abstract class Request<E> {
		public static enum Method {
			GET,
			POST,
		};

		protected final Method method;
		protected final Uri uri;

		/**
		 * コンストラクタ
		 * @param method メソッド
		 * @param uri 接続先URI
		 */
		public Request(Method method, Uri uri) {
			this.method = method;
			this.uri = uri;
		}

		/**
		 * 接続タイムアウト値を取得する。
		 * @return 接続タイムアウト値(ミリ秒)
		 */
		protected int getConnectTimeout() throws Exception {
			return 0;
		}

		/**
		 * 受信タイムアウト値を取得する。
		 * @return 受信タイムアウト値(ミリ秒)
		 */
		protected int getReadTimeout() throws Exception {
			return 0;
		}

		/**
		 * 追加ヘッダを取得する。
		 * @return 追加ヘッダの配列
		 */
		protected Map<String, List<String>> getAdditionalHeaders() { return null; }

		/**
		 * リクエストボディを取得する。
		 * @return Entityオブジェクト
		 */
		protected Entity getEntity() throws Exception {
			return null;
		}

		protected abstract E onResponse(InputStream in) throws Exception;
	}

	public static class Response<E> {
		protected int statusCode = -1;
		protected String message = null;
		protected final Map<String, List<String>> headers = new HashMap<>();
		protected E data = null;
		protected String body = null;

		/**
		 * HTTPステータスコードを取得する。
		 * @return HTTPステータスコード
		 */
		public int getResponseCode() {
			return statusCode;
		}

		/**
		 * HTTP応答メッセージを取得する。
		 * @return メッセージ
		 */
		public String getResponseMessage() {
			return message;
		}

		/**
		 * HTTP応答ヘッダを取得する。
		 * @return ヘッダリスト
		 */
		public Map<String, List<String>> getHeaders() {
			return headers;
		}

		/**
		 * データを取得する。
		 * @return データ
		 */
		public E getData() {
			return data;
		}

		/**
		 * エラー応答時のボディデータを取得する。
		 * @return ボディデータ
		 */
		public String getBody() {
			return body;
		}
	}

	public static abstract class Task<E> implements Callable<Response<E>> {
		private final Request<E> req;

		protected Task(Request<E> req) {
			this.req = req;
		}

		protected abstract Response<E> call(Request<E> req) throws Exception;

		@Override
		public Response<E> call() throws Exception {
			Response<E> res;
			if(req instanceof Mock) {
				res = new Response<E>();
				res.statusCode = 200;
				res.data = req.onResponse(((Mock)req).createResponse());
			} else {
				res = call(req);
			}
			android.util.Log.v("test", req.uri.toString() + "--->" + res.getResponseCode());
			return res;
		}
	}

	private class TaskExecutor<E> extends FutureTask<Response<E>> {
		private final Listener<E> listener;

		public TaskExecutor(Listener<E> listener, Callable<Response<E>> callable) {
			super(callable);
			this.listener = listener;
		}

		@Override
		public void done() {
			if(listener != null) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						if(isCancelled()) listener.onCancelled(TaskExecutor.this);
						else listener.onDone(TaskExecutor.this);
					}
				});
			}
		}
	}

	public interface Mock {
		InputStream createResponse() throws Exception;
	}

	public interface Listener<E> {
		void onDone(Future<Response<E>> future);
		void onCancelled(Future<Response<E>> future);
	}

	public static class SimpleListener<E> implements Listener<E> {
		@Override public void onDone(Future<Response<E>> future) {}
		@Override public void onCancelled(Future<Response<E>> future) {}
	}
}
