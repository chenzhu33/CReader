package com.sogou.ui;

import com.sogou.constdata.ConstData;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class MyMoveView extends ViewGroup {

	private final static int TOUCH_STATE_REST = 0;

	private final static int TOUCH_STATE_MOVING = 1;

	private final static int MOVE_TO_LEFT = 1;

	private final static int MOVE_TO_RIGHT = 2;

	private final static int MOVE_TO_REST = 0;

	public final static int MAIN = 0;
	public final static int LEFT = 1;
	public final static int RIGHT = 2;

	private int touch_state = TOUCH_STATE_REST;

	private int move_state = MOVE_TO_REST;

	private int now_state = MAIN;

	private final float WIDTH_RATE = 0.60f;
	private MainView main_show_view;
	private ItemView left_show_view;
	private ItemView right_show_view;

	private int min_distance = 100;

	private int screen_w;
	private int screen_h;

	private int move_x_v;

	private boolean isAimationMoving = false;

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			synchronized (MyMoveView.this) {
				isAimationMoving = true;
				int move_change = (int) (screen_w * WIDTH_RATE / 5);
				int left = main_show_view.getView().getLeft();
				if (msg.what == 1) {
					move(move_change + left);
				}
				if (msg.what == 11) {
					isAimationMoving = false;
					moveToLeft(false);
				}
				if (msg.what == 2) {
					move(-1 * move_change + left);
				}
				if (msg.what == 12) {
					isAimationMoving = false;
					moveToRight(false);
				}
				if (msg.what == 0) {
					if (now_state == LEFT) {
						move(-1 * move_x_v);
					} else {
						move(move_x_v);
					}
				}
				if (msg.what == 10) {
					isAimationMoving = false;
					moveToMain(false);
				}
			}
		}
	};

	public MyMoveView(Context context) {
		super(context);
	}

	public MyMoveView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyMoveView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void initView() {
		if (main_show_view == null) {
			main_show_view = new MainView(this.getContext(), this);
			left_show_view = new ItemView(this.getContext(),
					ConstData.book_cate, ConstData.goto_data);
			right_show_view = new ItemView(this.getContext(),
					ConstData.book_rank, ConstData.goto_data_rank);
		}

		this.addView(left_show_view.getView());
		this.addView(right_show_view.getView());
		this.addView(main_show_view.getView());

	}

	public void initContent() {

	}

	public void move(int start) {
		int left = main_show_view.getView().getLeft();
		if (now_state == MAIN) {

			if (left > 0) {
				if (move_state != MOVE_TO_LEFT) {
					move_state = MOVE_TO_LEFT;
				}
				left_show_view.getView().setVisibility(View.VISIBLE);
				right_show_view.getView().setVisibility(View.GONE);
			} else if (left < 0) {
				if (move_state != MOVE_TO_RIGHT) {
					move_state = MOVE_TO_RIGHT;
				}
				right_show_view.getView().setVisibility(View.VISIBLE);
				left_show_view.getView().setVisibility(View.GONE);
			} else {
				move_state = MOVE_TO_REST;
			}
			main_show_view.getView().layout(start, 0, start + screen_w,
					screen_h);
		} else {
			left = (int) (screen_w * WIDTH_RATE);
			if (now_state == RIGHT) {
				left = -1 * left;
			}
			left = left + start;
			main_show_view.getView().layout(left, 0, left + screen_w, screen_h);
		}
	}

	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		if (move_state == MOVE_TO_REST) {
			if (now_state == MAIN) {
				int w = (int) (screen_w * WIDTH_RATE);
				main_show_view.getView().layout(0, 0, screen_w, screen_h);
				left_show_view.getView().layout(0, 0, w, screen_h);
				right_show_view.getView().layout(screen_w - w, 0, screen_w,
						screen_h);
			} else if (now_state == LEFT) {
				moveToLeft(false);
			} else {
				moveToRight(false);
			}
		}
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		main_show_view.getView().measure(widthMeasureSpec, heightMeasureSpec);
		left_show_view.getView().measure(MeasureSpec.UNSPECIFIED,
				heightMeasureSpec);
		right_show_view.getView().measure(MeasureSpec.UNSPECIFIED,
				heightMeasureSpec);
		left_show_view.setWidth((int) (screen_w * WIDTH_RATE));
		right_show_view.setWidth((int) (screen_w * WIDTH_RATE));
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	}

	private int start_x;
	private int start_y;
	private boolean isMoved;

	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (isAimationMoving) {
			return super.dispatchTouchEvent(ev);
		} else {
			int action = ev.getAction();
			float x = ev.getX();
			float y = ev.getY();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				super.dispatchTouchEvent(ev);
				start_y = (int) y;
				move_x_v = 0;
				if (this.touch_state == TOUCH_STATE_REST) {
					this.touch_state = TOUCH_STATE_MOVING;
					start_x = (int) x;
					isMoved = false;
					move_state = MOVE_TO_REST;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				int last_y = (int) y;
				int last_x = (int) x;
				super.dispatchTouchEvent(ev);
				if (!isMoved) {
					if (Math.abs(last_y - start_y) > Math.abs(last_x - start_x)) {
						super.onTouchEvent(ev);
						return true;
					} else {
						if (Math.abs(last_x - start_x) > 10) {
							isMoved = true;
						}
					}
				}
				if (isMoved) {
					if (this.touch_state == TOUCH_STATE_MOVING) {
						if (Math.abs(last_x - start_x) > 10) {
							isMoved = true;
							int move_x = last_x - start_x;
							if (move_x > 0 && now_state == LEFT) {
								isMoved = false;
								break;
							}
							if (move_x < 0 && now_state == RIGHT) {
								isMoved = false;
								break;
							}

							move(move_x);

						}
					}
					return false;
				}
				break;
			case MotionEvent.ACTION_UP:
				if (this.touch_state == TOUCH_STATE_MOVING) {
					if (isMoved) {
						last_x = (int) x;
						if (Math.abs(last_x - start_x) > min_distance) {
							if (now_state == MAIN) {
								if (move_state == MOVE_TO_LEFT) {
									this.moveToLeft(false);
								}
								if (move_state == MOVE_TO_RIGHT) {
									this.moveToRight(false);
								}
							} else {
								this.moveToMain(false);
							}
						} else {
							if (now_state == MAIN) {
								this.moveToMain(false);
							}
							if (now_state == LEFT) {
								this.moveToLeft(false);
							}
							if (now_state == RIGHT) {
								this.moveToRight(false);
							}
						}
						move_state = MOVE_TO_REST;
					} else {
						super.dispatchTouchEvent(ev);
						this.touch_state = TOUCH_STATE_REST;
						return false;
					}
				}
				super.onTouchEvent(ev);
				this.touch_state = TOUCH_STATE_REST;
				break;
			}
			return true;
		}
	}

	public boolean getIsMoved() {
		return isMoved;
	}

	public void moveToLeft(boolean b) {
		if (!b) {
			int move_x = (int) (screen_w * WIDTH_RATE);
			left_show_view.getView().layout(0, 0, screen_w, screen_h);
			right_show_view.getView().layout(move_x, 0, move_x * 2, screen_h);
			main_show_view.getView().layout(move_x, 0, move_x + screen_w,
					screen_h);
			now_state = LEFT;
		} else {
			mHandler.postDelayed(new Runnable() {

				public void run() {
					int move_change = (int) (screen_w * WIDTH_RATE / 5);
					int left = (int) (screen_w * WIDTH_RATE - main_show_view
							.getView().getLeft());
					Message msg = new Message();
					if (left > move_change) {
						msg.what = 1;
						mHandler.sendMessage(msg);
						mHandler.postDelayed(this, 10);
					} else {
						msg.what = 11;
						mHandler.sendMessage(msg);
						mHandler.removeCallbacks(this);
					}
				}
			}, 0);
		}
	}

	public void moveToRight(boolean b) {
		if (!b) {
			int move_x = (int) (screen_w * WIDTH_RATE) * -1;
			left_show_view.getView().layout(screen_w + 2 * move_x, 0,
					move_x + screen_w, screen_h);
			right_show_view.getView().layout(screen_w + move_x, 0, screen_w,
					screen_h);
			main_show_view.getView().layout(move_x, 0, move_x + screen_w,
					screen_h);
			now_state = RIGHT;
		} else {
			mHandler.postDelayed(new Runnable() {

				public void run() {
					int move_change = (int) (screen_w * WIDTH_RATE / 5);
					int left = (int) (screen_w * WIDTH_RATE + main_show_view
							.getView().getLeft());
					Message msg = new Message();
					if (left > move_change) {
						msg.what = 2;
						mHandler.sendMessage(msg);
						mHandler.postDelayed(this, 10);
					} else {
						msg.what = 12;
						mHandler.sendMessage(msg);
						mHandler.removeCallbacks(this);
					}
				}
			}, 0);
		}
	}

	public void moveToMain(boolean b) {
		if (!b) {
			right_show_view.getView().setVisibility(View.VISIBLE);
			left_show_view.getView().setVisibility(View.VISIBLE);
			int w = (int) (screen_w * WIDTH_RATE);
			main_show_view.getView().layout(0, 0, screen_w, screen_h);
			left_show_view.getView().layout(0, 0, w, screen_h);
			right_show_view.getView().layout(screen_w - w, 0, screen_w,
					screen_h);
			now_state = MAIN;
		} else {
			move_x_v = 0;
			mHandler.postDelayed(new Runnable() {

				public void run() {
					int move_change = (int) (screen_w * WIDTH_RATE / 5);
					int left = Math.abs(main_show_view.getView().getLeft()) - 0;
					Message msg = new Message();
					if (left > move_change) {
						msg.what = 0;
						move_x_v = move_x_v + move_change;
						mHandler.sendMessage(msg);
						mHandler.postDelayed(this, 10);
					} else {
						msg.what = 10;
						mHandler.sendMessage(msg);
						mHandler.removeCallbacks(this);
					}
				}
			}, 0);
		}
	}

	public void initScreenSize(int w, int h) {
		this.screen_w = w;
		this.screen_h = h;
		this.setKeepScreenOn(true);
		min_distance = (int) (screen_w / 6.0);
		initView();
		initContent();
		moveToMain(false);
	}

	public int getNowState() {
		return this.now_state;
	}
}
