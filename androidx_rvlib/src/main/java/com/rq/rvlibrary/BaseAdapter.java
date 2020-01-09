package com.rq.rvlibrary;

import android.content.Context;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rq.rvlibrary.BaseViewHolder.TAG_POSITION;

/**
 * Created by raoqian on 2018/9/21
 */

public class BaseAdapter<DATA, VH extends BaseViewHolder> extends RecyclerView.Adapter<VH> {
    protected SparseArray<Class<? extends BaseViewHolder>> multipleHolder;
    Context mContext;
    Class<?> mHolder;
    private int itemId;
    private Object mObject;
    private List<DATA> showData = new ArrayList<>();
    private SparseArray<Class<? extends BaseViewHolder>> headType = new SparseArray<>();//顶部视图处理器类型
    private SparseArray<Object> headViewData = new SparseArray<>();//顶部视图数据
    private SparseIntArray headViewResId = new SparseIntArray();
    private SparseArray<Class<? extends BaseViewHolder>> footType = new SparseArray<>();//底部视图处理器类型
    private SparseArray<Object> footViewData = new SparseArray<>();//底部视图数据
    private SparseIntArray footViewResId = new SparseIntArray();
    private ActionPasser mActionPasser;
    private HashMap<Class, OnClickMaker> clicks = new HashMap<>();
    private OnClickMaker allOnClickInfo;
    private OnAttachedToBottomListener mOnAttachedToBottomListener;
    private Map<String, Object> contentCash = new HashMap<>();

    private void setContext(Context context) {
        this.mContext = context;
    }

    public BaseAdapter(Context context, @LayoutRes int itemLayoutId, Class<? extends BaseViewHolder> baseViewHolderClass) {
        this(context, itemLayoutId, baseViewHolderClass, null);
    }

    /**
     * 单布局类型  只要一种子View setData 数据条数大于 0  才会显示
     *
     * @param itemLayoutId 对 BrandHolder 的描述，必须与 BaseViewHolder 使用保持一致
     */
    public BaseAdapter(Context context, @LayoutRes int itemLayoutId, Class<? extends BaseViewHolder> baseViewHolderClass, Object obj) {
        if (context == null || baseViewHolderClass == null || itemLayoutId == 0) {
            throw new AdapterUseException("BaseAdapter.使用三参数构造函数 值不能为空");
        }
        setContext(context);
        this.mHolder = baseViewHolderClass;
        this.itemId = itemLayoutId;
        this.mObject = obj;
    }


    /**
     * 不建议使用，每创建见一个ViewHolder会动用两次反射{@link BaseAdapter#getViewHolderByClass(Class, int, ViewGroup, int)}，
     * 布局ID通过重写{@link BaseViewHolder#inflateLayoutId()}指定
     */
    @Deprecated
    public BaseAdapter(Context context, Class<? extends BaseViewHolder> baseViewHolderClass, Object obj) {
        if (context == null || baseViewHolderClass == null) {
            throw new AdapterUseException("BaseAdapter.使用三参数构造函数 值不能为空");
        }
        setContext(context);
        this.mHolder = baseViewHolderClass;
        this.mObject = obj;
    }

    /**
     * 无序布局   子视图有多种类型
     * 不固定位置
     *
     * @param maps key  对 BrandHolder 的描述，value key布局对应的 BaseViewHolder.class，必须与 BaseViewHolder 使用保持一致
     */
    public BaseAdapter(Context context, @NonNull SparseArray<Class<? extends BaseViewHolder>> maps, Object... innerClassContext) {
        setContext(context);
        this.multipleHolder = maps;
        if (innerClassContext != null && innerClassContext.length > 0) {
            this.mObject = innerClassContext[0];
        }
        getMultipleHolderType(null, 0);//进行检测
    }

    /**
     * 追加型布局
     */
    public BaseAdapter(Context context) {
        setContext(context);
    }

    public void changeItemView(int viewLayout, boolean refuse) {
        this.itemId = viewLayout;
        if (refuse) {
            notifyDataSetChanged();
        }
    }

    /**
     * 直接刷新视图，数据为空将置空列表
     *
     * @param dataList 填充数据
     */
    public void setData(List dataList) {
        this.showData.clear();
        if (dataList != null) {
            this.showData = dataList;
        }
        this.notifyDataSetChanged();
    }

    public void addData(List dataList) {
        if (dataList != null) {
            this.showData.addAll(dataList);
            this.notifyDataSetChanged();
        }
    }

    public DATA getDataItem(int position) {
        if (position >= 0 && position < showData.size()) {
            return showData.get(position);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (multipleHolder != null) {
            return getMultipleHolderType(getDataItem(position), position);
        }
        if (position < headViewData.size() || position >= headViewData.size() + showData.size()) {
            return position * -1 - 1;
        }

        return position;
    }

    /**
     * @param dataItem 数据内容
     * @param position 数据位置
     * @return 返回布局Id
     */
    protected int getMultipleHolderType(DATA dataItem, int position) {
        throw new AdapterUseException(" 多类型布局使用错误，必须复写 getMultipleHolderType() 方法,并且不调用父类方法  ");
    }


    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        VH viewHolder;
        if (multipleHolder != null) {
            Class clazz = multipleHolder.get(viewType);
            if (clazz == null) {
                throw new AdapterUseException(" 多类型布局使用错误，multipleHolder key 为 R.layout.id   value 为 Holder.class  ");
            }
            viewHolder = getViewHolderByClass(clazz, viewType, parent, viewType);
        } else if (viewType < 0) {
            int realPosition = (viewType + 1) * -1;
            if (realPosition < headType.size()) {
                final Class<?> holderRoot = headType.get(realPosition);
                LOG.e("BaseAdapter", "onCreateViewHolder.viewType: " + viewType + "  " + holderRoot);
                viewHolder = getViewHolderByClass(holderRoot, headViewResId.get(realPosition), parent, realPosition);
            } else {
                int tagPosition = realPosition - headViewData.size() - showData.size();
                viewHolder = getViewHolderByClass(footType.get(tagPosition), footViewResId.get(tagPosition), parent, realPosition);
            }
        } else {
            viewHolder = getViewHolderByClass(mHolder, this.itemId, parent, viewType);
        }
        if (viewHolder != null) {
            LOG.e("OnClickMaker", viewHolder.getClass() + " >> " + clicks.get(viewHolder.getClass()));
            OnClickMaker itemOnClickMaker = clicks.get(viewHolder.getClass());
            if (allOnClickInfo != null) {
                viewHolder.setClickInfo(allOnClickInfo);
            } else if (itemOnClickMaker != null) {
                viewHolder.setClickInfo(itemOnClickMaker);
            }
            viewHolder.setContext(mContext);
            if (viewHolder.itemView.getLayoutParams() != null) {
                viewHolder.itemView.setLayoutParams(viewHolder.getLMLayoutParams(viewHolder.itemView.getLayoutParams()));
            }
        }
        return viewHolder;
    }

    private Object getMore() {
        return mObject;
    }

    /**
     * 通过反射获取Holder实例
     */
    protected VH getViewHolderByClass(@NonNull Class<?> holderRoot, @LayoutRes int resId, ViewGroup parent, int viewType) {
        String error = "";
        try {
            Constructor<?>[] ctors = holderRoot.getDeclaredConstructors();
            if (ctors != null && ctors.length > 0) {
                int realLayoutId;
                if (resId != 0) {
                    realLayoutId = resId;
                } else {
                    LOG.e("BaseAdapter", "LINE(213):");
                    VH holder = getVH(ctors[0], new View(mContext));
                    realLayoutId = holder.inflateLayoutId();
                }
                View itemView = LayoutInflater.from(mContext).inflate(realLayoutId, parent, false);
                VH holder = getVH(ctors[0], itemView);
                holder.setRecyclerView(parent);
                if (mActionPasser != null) {
                    holder.setPasser(mActionPasser);
                }
                return holder;
            }
        } catch (InstantiationException e) {
            error = e.getMessage();
        } catch (IllegalAccessException e) {
            error = e.getMessage();
        } catch (Exception e) {
            error = e.getMessage();
        }
        if (error != null && error.contains("Wrong number of arguments")) {
            error = error + "\n【【【" + holderRoot.getSimpleName() + ".调用类内部类ViewHolder 调用四参数构造方法 或者 重写 getMore() 内容 】】】";
        }
        throw new AdapterUseException(holderRoot.getSimpleName() + " 初始化异常:" + error);
    }

    private VH getVH(Constructor<?> ctor, View itemView) throws Exception {
        VH holder;
        try {
            holder = (VH) ctor.newInstance(itemView);
        } catch (IllegalArgumentException e) {
            if (getMore() != null) {
                try {
                    holder = (VH) ctor.newInstance(getMore(), itemView);
                } catch (Exception e1) {
                    throw e1;
                }
            } else {
                throw e;
            }
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.itemView.setTag(TAG_POSITION, position);
        if (holder instanceof OnContentKeeper) {
            LOG.e("onBindViewHolder", "addView");
            LOG.e("BaseAdapter", "onBindViewHolder: " + holder.getPosition());
            useCash(holder);
            return;
        }
        if (multipleHolder != null) {
            holder.fillObject(getDataItem(position));
        } else if (position < headViewData.size()) {
            Object headObj = headViewData.get(position);
            if (headObj instanceof InnerList) {
                for (int i = 0; i < ((InnerList) headObj).size(); i++) {
                    holder.setObject(((InnerList) headObj).get(i));
                    holder.fillObject(((InnerList) headObj).get(i));
                }
            } else {
                holder.setObject(headObj);
                holder.fillObject(headObj);
            }
        } else if (position >= headViewData.size() + showData.size()) {
            Object footObj = footViewData.get(position);
            if (footObj instanceof InnerList) {
                for (int i = 0; i < ((InnerList) footObj).size(); i++) {
                    holder.setObject(((InnerList) footObj).get(i));
                    holder.fillObject(((InnerList) footObj).get(i));
                }
            } else {
                holder.setObject(footObj);
                holder.fillObject(footObj);
            }
        } else {
            holder.setData(getDataItem(position - headViewData.size()));
            holder.fillData(position - headViewData.size(), getDataItem(position - headViewData.size()));
        }
    }

    @Override
    public int getItemCount() {
        return showData.size() + headViewData.size() + footViewData.size();
    }

    /**
     * 由于 SparseArray 的特性，add***Holder只能从前面往后面追加，要增加指定顺序需更换为HasMap
     * 需要进行替换，使用以下方法
     *
     * @see #setFootHolder(int, Object, Class, int, Object...)
     * @see #setHeadHolder(int, Object, Class, int, Object...)
     */
    public void addFootHolder(Object object, Class<? extends BaseViewHolder> footHolder, @LayoutRes int resId, Object... innerClassContext) {
        int oldSize = footType.size();
        footType.put(footType.size(), footHolder);
        footViewData.put(oldSize, object);
        footViewResId.put(oldSize, resId);
        if (innerClassContext != null && innerClassContext.length > 0) {
            this.mObject = innerClassContext[0];
        }
    }

    public void addFootHolder(Class<? extends BaseViewHolder> footHolder, @LayoutRes int resId, Object more) {
        addFootHolder(null, footHolder, resId, more);
    }

    public void addHeadHolder(Class<? extends BaseViewHolder> headHolder, @LayoutRes int resId, Object more) {
        addHeadHolder(null, headHolder, resId, more);
    }

    public void addHeadHolder(Object object, Class<? extends BaseViewHolder> headHolder, @LayoutRes int resId, Object... innerClassContext) {
        int oldSize = headType.size();
        headType.put(headType.size(), headHolder);
        headViewData.put(oldSize, object);
        headViewResId.put(oldSize, resId);
        if (innerClassContext != null && innerClassContext.length > 0) {
            this.mObject = innerClassContext[0];
        }
    }

    /**
     * @see #addFootHolder(Object, Class, int, Object...)
     */
    public void setFootHolder(int position, Object object, Class<? extends BaseViewHolder> footHolder, @LayoutRes int resId, Object... innerClassContext) {
        if (footType.size() < position) {
            addHeadHolder(object, footHolder, resId, innerClassContext);
            notifyDataSetChanged();
            return;
        }
        InnerList newData = new InnerList();
        Object oldData = footViewData.get(position);
        if (oldData instanceof InnerList) {
            newData.addAll((InnerList) oldData);
            newData.add(object);
        } else {
            newData.add(oldData);
            newData.add(object);
        }
        footType.put(position, footHolder);
        footViewData.put(position, newData);
        footViewResId.put(position, resId);
        if (innerClassContext != null && innerClassContext.length > 0) {
            this.mObject = innerClassContext[0];
        }
        notifyItemChanged(position + headType.size() + getItemCount());
    }

    /**
     * @param position          头序号，与数据序号无关
     * @param object            数据
     * @param headHolder        文件
     * @param resId             布局
     * @param innerClassContext 上下文
     */
    public void setHeadHolder(int position, Object object, Class<? extends BaseViewHolder> headHolder, @LayoutRes int resId, Object... innerClassContext) {
        if (headType.size() < position) {
            addHeadHolder(object, headHolder, resId, innerClassContext);
            notifyDataSetChanged();
            return;
        }
        InnerList newData = new InnerList();
        Object oldData = headViewData.get(position);
        if (oldData instanceof InnerList) {
            newData.addAll((InnerList) oldData);
            newData.add(object);
        } else {
            newData.add(oldData);
            newData.add(object);
        }
        headType.put(position, headHolder);
        headViewData.put(position, newData);
        headViewResId.put(position, resId);
        if (innerClassContext != null && innerClassContext.length > 0) {
            this.mObject = innerClassContext[0];
        }
        notifyItemChanged(position);
    }

    public void clearHeadView() {
        headType.clear();
        headViewData.clear();
        headViewResId.clear();
        notifyDataSetChanged();
    }

    public void clearFootView() {
        footType.clear();
        footViewData.clear();
        footViewResId.clear();
        notifyDataSetChanged();
    }

    public void setActionPasser(ActionPasser passer) {
        this.mActionPasser = passer;
    }

//    public void addOnClick(Class<ThirdAppViewHolder> thirdAppViewHolderClass, ThirdController thirdController, int i, int iv_delete) {
//    }

    @Override
    public void onViewDetachedFromWindow(@NonNull VH holder) {
        if (holder instanceof OnContentKeeper) {
            setCash(holder);
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull VH holder) {
        if (holder instanceof OnContentKeeper) {
            useCash(holder);
        }
        if (holder.getAdapterPosition() == getItemCount() - 1) {
            onAttachedToBottom(holder.getAdapterPosition());
        } else if (holder.getAdapterPosition() == 0) {
            onAttachedToTop();
        }
    }

    public void removeItem(int deletePosition) {
        if (deletePosition >= 0 && showData.size() > 0 && showData.size() > deletePosition) {
            showData.remove(deletePosition);
            notifyDataSetChanged();
        }
    }

    // id = 0   或者不设置 id 则将点击事件绑定到最外层 ItemView
    public <T extends BaseViewHolder> void addOnClick(Class<T> clazz, OnItemClickListener clickListener, int... ids) {
        if (clazz != null && clickListener != null) {
            clicks.put(clazz, new OnClickMaker(clickListener, ids));
        } else if (clickListener != null) {
            setChildClick(clickListener, ids);
        }
    }

    public void setChildClick(OnItemClickListener clickListener, int... ids) {
        allOnClickInfo = new OnClickMaker(clickListener, ids);
    }

    public void setOnAttachedToBottomListener(OnAttachedToBottomListener l) {
        this.mOnAttachedToBottomListener = l;
    }

    protected void onAttachedToBottom(int position) {
        if (mOnAttachedToBottomListener != null) {
            mOnAttachedToBottomListener.onAttachedToBottom(position);
        }
        LOG.d("BaseAdapter", "onAttachedToBottom: ");
    }

    public interface DisplayOption<DATA> {
        /**
         * @param bean     需要判断的数据
         * @param rule     提供的判断条件
         * @param position
         * @return 是否显示该数据
         */
        boolean show(DATA bean, Object rule, int position);
    }

    private DisplayOption mDisplayOption;

    public void setDisplay(DisplayOption mDisplayOption) {
        this.mDisplayOption = mDisplayOption;
    }

    List<DATA> originData = new ArrayList<>();

    /**
     * you must used {@link #setDisplay(DisplayOption)} before display
     *
     * @return 是否刷新视图
     */
    public boolean display(Object rule) {
        final int showSize = showData.size();
        final int originSize = originData.size();
        if (mDisplayOption == null) {
            LOG.e("BaseAdapter", "display error:you must used setDisplay before display");
            return false;
        }
        if (rule == null) {
            if (originData.size() > showData.size()) {
                setData(deepCopy(originData));
                return true;
            }
            return false;
        }
        if (showSize > originSize) {
            originData.clear();
            originData.addAll(showData);
        }
        boolean hasChange = false;
        List<DATA> newShow = new ArrayList<>();
        for (int position = 0; position < originData.size(); position++) {
            if (mDisplayOption.show(originData.get(position), rule, position)) {
                newShow.add(originData.get(position));
                hasChange = true;
            }
        }
        setData(newShow);
        return hasChange;
    }

    public static <E> List<E> deepCopy(List<E> src) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(src);

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            @SuppressWarnings("unchecked")
            List<E> dest = (List<E>) in.readObject();
            return dest;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    protected void onAttachedToTop() {
        LOG.d("BaseAdapter", "onAttachedToTop: ");
    }

    /**
     * ##########################################################################
     * RecyclerView 快速滑动时 EditText 等视图内容会随着视图复用而造成数据多余
     * Holder.class 实现 OnContentKeeper 即可解决此问题
     * ##########################################################################
     */

    public void setCash(VH holder) {//存储数据
        if (((OnContentKeeper) holder).getSaveViewId() == null || ((OnContentKeeper) holder).getSaveViewId().length == 0) {
            return;
        }
        for (int saveContentId : ((OnContentKeeper) holder).getSaveViewId()) {
            Object save = ((OnContentKeeper) holder).getSave(saveContentId);
            if (save != null) {
                contentCash.put(holder.getPosition() + "" + saveContentId, save);
                LOG.e("BaseAdapter", holder.getPosition() + " setCash: " + save);
            }
        }
    }

    public void useCash(VH holder) {//使用存储数据填充视图
        if (((OnContentKeeper) holder).getSaveViewId() == null || ((OnContentKeeper) holder).getSaveViewId().length == 0) {
            return;
        }
        for (int saveContentId : ((OnContentKeeper) holder).getSaveViewId()) {
            Object value = contentCash.get(holder.getPosition() + "" + saveContentId);
            ((OnContentKeeper) holder).onRelease(value, saveContentId);
        }
    }

    public interface OnAttachedToBottomListener {
        void onAttachedToBottom(int position);
    }

    class OnClickMaker {

        OnItemClickListener clickListener;
        int[] clickIds;

        public OnClickMaker(OnItemClickListener clickListener, int[] ids) {
            this.clickListener = clickListener;
            this.clickIds = ids;
        }
    }

    public interface OnItemClickListener<DATA> {
        void onClick(DATA data, View view);
    }

}
