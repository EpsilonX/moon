package com.shunote;

/**
 * 对节点进行长按拖动和向左滑动删除节点处理
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class ListViewDrag extends ListView {

	private Context mContext;
	private DropListener mDropListener;
	private RemoveListener mRemoveListener;
	private GestureDetector mGestureDetector;
	private boolean mDialogVisible = false; // 判断对话框是否已经弹出

	private ImageView mDragView;
	private int mDragPos; // which item is being dragged
	private int mFirstDragPos; // where was the dragged item originally
	private int mDragPointX; // 相对于item的x坐标
	private int mDragPointY; // at what offset inside the item did the user grab
								// it
	private int mCoordOffsetX;
	private int mCoordOffsetY; // the difference between screen coordinates and
								// coordinates in this view

	private Rect mTempRect = new Rect();
	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mWindowParams;
	private Bitmap mDragBitmap;
	// private int mItemHeightHalf = 32;
	private int mItemHeightNormal = 64;

	// private int mItemHeightExpanded = 128;

	public ListViewDrag(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
		mContext = context;

	}

	public ListViewDrag(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 重写长按事件,可以对item进行拖动
	 */
	public void setOnItemLongClickListener(final MotionEvent ev, int xx, int yy) {

		final int x = xx;
		final int y = yy;

		setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				parent.getAdapter().getItem(position);

				int itemnum = position;

				// 屏蔽headView的长按事件--headView不能长按.
				if (id != -1) {
					// 添加震动反馈
					view.performHapticFeedback(
							HapticFeedbackConstants.LONG_PRESS,
							HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);

					if (itemnum == AdapterView.INVALID_POSITION) {
						return false;
					}
					ViewGroup item = (ViewGroup) getChildAt(itemnum
							- getFirstVisiblePosition());

					// 如果listView显示的第一个view是headView,进行判断后再拖拽
					// if (getFirstVisiblePosition() == 0) {
					//
					// int head = getFirstVisiblePosition();
					// // 计算headView的高低
					// ViewGroup headview = (ViewGroup) getChildAt(head);
					// int headheight = headview.getTop();
					// mDragPointY = y - item.getTop();
					//
					// // return false;
					// }
					// // 如果listView显示的第一个view不是headView,进行普通的拖拽.
					// else {
					mDragPointX = x - item.getLeft();
					mDragPointY = y - item.getTop();

					mCoordOffsetX = ((int) ev.getX()) - x;
					mCoordOffsetY = ((int) ev.getRawY()) - y;

					item.setDrawingCacheEnabled(true);
					// Create a copy of the drawing cache so that it does
					// not
					// get recycled
					// by the framework when the list tries to clean up
					// memory
					Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());
					startDragging(bitmap, x, y);
					// 需要进行-1，否则拖拽的是下一个item.
					mDragPos = itemnum - 1;
					mFirstDragPos = mDragPos;
				}
				// }
				return true;
			}

		});

	}

	/**
	 * 接受滑动删除和长按拖拽事件
	 */

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		if (mDropListener != null) {

			if (ev.getAction() == MotionEvent.ACTION_DOWN) {
				int x = (int) ev.getX();
				int y = (int) ev.getY();
				setOnItemLongClickListener(ev, x, y);
			}
		}

		if (mRemoveListener != null && mGestureDetector == null) {

			mGestureDetector = new GestureDetector(getContext(),
					new SimpleOnGestureListener() {

						@Override
						public boolean onFling(MotionEvent e1, MotionEvent e2,
								float velocityX, float velocityY) {

							if (e1.getX() - e2.getX() > 150
									&& Math.abs(velocityY) > 150) {

								int x1 = (int) e1.getX();
								int y1 = (int) e1.getY();
								final int itemnum = pointToPosition(x1, y1);

								if (itemnum != 0) {

									// 得到当前按下的item到ListView顶部的ViewGroup
									ViewGroup item = (ViewGroup) getChildAt(itemnum
											- getFirstVisiblePosition());

									final View del = item
											.findViewById(R.id.nodelist_relat1);
									del.setVisibility(View.VISIBLE);

									Button delete = (Button) del
											.findViewById(R.id.nodelist_b2);
									delete.setVisibility(View.VISIBLE);

									delete.setOnClickListener(new View.OnClickListener() {

										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											AlertDialog.Builder builder = new AlertDialog.Builder(
													mContext);
											builder.setTitle("是否要删除该节点？");
											builder.setPositiveButton(
													"确定",
													new DialogInterface.OnClickListener() {

														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															// TODO
															// Auto-generated
															// method stub
															stopDragging();
															mRemoveListener
																	.remove(itemnum - 1);
															unExpandViews(true);

														}
													});
											builder.setNegativeButton(
													"取消",
													new DialogInterface.OnClickListener() {

														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															// TODO
															// Auto-generated
															// method stub

															del.setVisibility(View.INVISIBLE);

														}
													});
											builder.create().show();
										}
									});

								}
							}
							// flinging while dragging should have no effect
							return true;

						}
					});
		}

		return false;
	}

	/*
	 * 创建悬浮窗口View
	 * 
	 * 改变LayoutParams的属性并且刷新view
	 */
	private void startDragging(Bitmap bm, int x, int y) {
		stopDragging();

		mWindowParams = new WindowManager.LayoutParams();
		mWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
		mWindowParams.x = x - mDragPointX + mCoordOffsetX;
		mWindowParams.y = y - mDragPointY + mCoordOffsetY;

		mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

		// 悬浮窗口的位图格式,默认为不透明.通过PixelFormat来实现
		mWindowParams.format = PixelFormat.TRANSLUCENT;
		mWindowParams.windowAnimations = 0;

		ImageView v = new ImageView(getContext());
		// int backGroundColor = getContext().getResources().getColor(
		// R.drawable.icon);

		// 设置imageview的背景颜色
		v.setBackgroundDrawable(getSelector());
		v.setImageBitmap(bm);
		mDragBitmap = bm;

		mWindowManager = (WindowManager) getContext()
				.getSystemService("window");
		mWindowManager.addView(v, mWindowParams);
		mDragView = v;
	}

	// 判断mDragView和mDragBitmap是否为空,不为空则全部删除
	private void stopDragging() {
		if (mDragView != null) {
			WindowManager wm = (WindowManager) getContext().getSystemService(
					"window");
			wm.removeView(mDragView);
			mDragView.setImageDrawable(null);
			mDragView = null;
		}
		if (mDragBitmap != null) {
			mDragBitmap.recycle();
			mDragBitmap = null;
		}
	}

	/**
	 * 处理拖动和删除事件
	 */

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		if (mGestureDetector != null) {
			mGestureDetector.onTouchEvent(ev);
		}

		if (mDragView != null && mDragPos != AdapterView.INVALID_POSITION
				&& mDropListener != null) {
			int action = ev.getAction();
			switch (action) {

			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
				int x = (int) ev.getX();
				int y = (int) ev.getY();
				dragView(x, y);

				int itemnum = getItemForPosition(y);
				if (itemnum >= 0) {
					if (action == MotionEvent.ACTION_DOWN
							|| itemnum != mDragPos) {

						mDragPos = itemnum;
						doExpansion();
						Log.v(">>>doExpansion", ">>>>>>>>>>doExpansion");
					}
				}
				break;

			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				Rect r = mTempRect;
				mDragView.getDrawingRect(r);
				stopDragging();

				// 设置一个mDraPos的位置判断,为0的时候(父节点),暂时不进行拖动结果的排序,被拖动的item还是在原位置不变,
				// 一旦进行但是一旦点击"确定"的按钮之后,删除该item,然后做上一层处理;点击"取消"按钮，不做任何处理.

				if (mDropListener != null && mDragPos > 0
						&& mDragPos < getCount()) {
					mDropListener.drop(mFirstDragPos, mDragPos);
				}
				unExpandViews(false);
				break;
			}
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * 更新ViewLayout
	 * 
	 * @param x
	 * @param y
	 */
	private void dragView(int x, int y) {
		float alpha = 1.0f;
		mWindowParams.alpha = alpha;
		// }
		mWindowParams.x = x - mDragPointX + mCoordOffsetX;
		mWindowParams.y = y - mDragPointY + mCoordOffsetY;
		mWindowManager.updateViewLayout(mDragView, mWindowParams);
	}

	/*
	 * 计算移动时具体到某一个位置(在某一个item的位置)
	 */
	private int getItemForPosition(int y) {
		int adjustedy = y - mDragPointY;
		int pos = pointToPosition(0, adjustedy);
		if (pos >= 0) {

			/**
			 * 关键之处!!判断移动的位置是否在父节点的范围,如果处于父节点的范围,则只弹出对话框提示
			 */
			if (pos == 0) {

				if (mDialogVisible == false) {

					AlertDialog dialog;
					AlertDialog.Builder builder = new AlertDialog.Builder(
							mContext);

					builder.setTitle("是否移动到上一层？");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									stopDragging();
									int itemnum = mFirstDragPos;
									mRemoveListener.remove(itemnum);
									unExpandViews(true);
									mDialogVisible = false;
								}
							});
					builder.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									mDialogVisible = false;

								}
							});

					dialog = builder.create();
					dialog.show();
					mDialogVisible = true;
				} else {
					mDialogVisible = true;
				}
			}

			if (pos <= mFirstDragPos) {
				pos += 1;
			}

		} else if (adjustedy < 0) {
			pos = 0;
		}
		return pos;
	}

	/*
	 * Restore size and visibility for all listitems
	 * 
	 * 修改删除某一个item后整个listView的layout,需要设置headView的高度固定不变,并且拖动的时候可以正常显示
	 */
	private void unExpandViews(boolean deletion) {
		// 需要从1开始,因为0是headView,我们不需要为其改变大小
		for (int i = 1;; i++) {
			View v = getChildAt(i);
			if (v == null) {
				if (deletion) {
					// HACK force update of mItemCount
					int position = getFirstVisiblePosition();
					int y = getChildAt(1).getTop();

					// 加入了headView,所以需要对adapter进行处理
					HeaderViewListAdapter listAdapter = (HeaderViewListAdapter) this
							.getAdapter();
					setAdapter(listAdapter.getWrappedAdapter());

					setSelectionFromTop(position, y);
					// end hack
				}
				layoutChildren(); // force children to be recreated where needed
				v = getChildAt(i);
				if (v == null) {
					break;
				}
			}
			ViewGroup.LayoutParams params = v.getLayoutParams();
			params.height = mItemHeightNormal;
			v.setLayoutParams(params);
			v.setVisibility(View.VISIBLE);
		}
	}

	/*
	 * Adjust visibility and size to make it appear as though an item is being
	 * dragged around and other items are making room for it: If dropping the
	 * item would result in it still being in the same place, then make the
	 * dragged listitem's size normal, but make the item invisible. Otherwise,
	 * if the dragged listitem is still on screen, make it as small as possible
	 * and expand the item below the insert point. If the dragged item is not on
	 * screen, only expand the item below the current insertpoint.
	 */
	private void doExpansion() {
		int childnum = mDragPos - getFirstVisiblePosition();

		if (mDragPos > mFirstDragPos) {
			childnum++;
		}

		View first = getChildAt(mFirstDragPos - getFirstVisiblePosition());

		// 需要从1开始,因为0是headView,我们不需要为其改变大小
		for (int i = 1;; i++) {
			View vv = getChildAt(i);
			if (vv == null) {
				break;
			}
			int height = mItemHeightNormal;
			int visibility = View.VISIBLE;
			if (vv.equals(first)) {
				// processing the item that is being dragged
				if (mDragPos == mFirstDragPos) {
					// hovering over the original location
					visibility = View.INVISIBLE;
				} else {
					// not hovering over it
					height = 1;
				}
			} else if (i == childnum) {
				if (mDragPos < getCount() - 1) {
					height = mItemHeightNormal;
				}
			}
			ViewGroup.LayoutParams params = vv.getLayoutParams();
			params.height = height;
			vv.setLayoutParams(params);
			vv.setVisibility(visibility);
		}
	}

	/*
	 * pointToPosition() doesn't consider invisible views, but we need to, so
	 * implement a slightly different version.
	 * 
	 * 拖动的时候可以从可视的view拖动到不可视的view进行交换
	 */
	// private int myPointToPosition(int x, int y) {
	// Rect frame = mTempRect;
	// final int count = getChildCount();
	// for (int i = count - 1; i >= 0; i--) {
	// final View child = getChildAt(i);
	// child.getHitRect(frame);
	// if (frame.contains(x, y)) {
	// return getFirstVisiblePosition() + i;
	// }
	// }
	// return INVALID_POSITION;
	// }

	public interface DropListener {

		void drop(int from, int to);

	}

	public void setDropListener(DropListener onDrop) {
		// TODO Auto-generated method stub
		mDropListener = onDrop;
	}

	public interface RemoveListener {
		void remove(int which);
	}

	public void setRemoveListener(RemoveListener remove) {
		mRemoveListener = remove;
	}

}