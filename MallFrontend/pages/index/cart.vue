<template>
  <s-layout :bgStyle="{ color: '#fff' }" tabbar="/pages/index/cart" :title="t('title')">
    <s-empty
      v-if="state.list.length === 0"
      icon="/static/cart-empty.png"
      :text="t('empty')"
    />

    <!-- 头部 -->
    <view v-if="state.list.length" class="cart-box ss-flex ss-flex-col ss-row-between">
      <view class="cart-header ss-flex ss-col-center ss-row-between ss-p-x-30">
        <view class="header-left ss-flex ss-col-center ss-font-26">
          {{ t('totalItems', { count: state.list.length }) }}
        </view>
        <view class="header-right">
          <button v-if="state.editMode" class="ss-reset-button" @tap="onChangeEditMode(false)">
            {{ t('cancel') }}
          </button>
          <button v-else class="ss-reset-button ui-TC-Main" @tap="onChangeEditMode(true)">
            {{ t('edit') }}
          </button>
        </view>
      </view>
      <!-- 内容 -->
      <view class="cart-content ss-flex-1 ss-p-x-30 ss-m-b-40">
        <view v-for="item in state.list" :key="item.id" class="goods-box ss-r-10 ss-m-b-14">
          <view class="ss-flex ss-col-center">
            <label class="check-box ss-flex ss-col-center ss-p-l-10" @tap="onSelectSingle(item.id)">
              <radio
                :checked="state.selectedIds.includes(item.id)"
                color="var(--ui-BG-Main)"
                style="transform: scale(0.8)"
                @tap.stop="onSelectSingle(item.id)"
              />
            </label>
            <view v-if="item.spu?.status !== 1 && !state.editMode" class="down-box">
              {{ t('goodsOffShelf') }}
            </view>
            <view v-else-if="item.spu?.stock <= 0 && !state.editMode" class="down-box">
              {{ t('goodsNoStock') }}
            </view>
            <s-goods-item
              :img="item.spu.picUrl || item.goods.image"
              :price="item.sku.price"
              :skuText="
                item.sku.properties.length > 1
                  ? item.sku.properties.reduce(
                      (items2, items) => items2.valueName + ' ' + items.valueName,
                    )
                  : item.sku.properties[0].valueName
              "
              :title="item.spu.name"
              :titleWidth="400"
              priceColor="#FF3000"
            >
              <template v-if="!state.editMode" v-slot:tool>
                <su-number-box
                  v-model="item.count"
                  :max="item.sku.stock"
                  :min="0"
                  :step="1"
                  @change="onNumberChange($event, item)"
                />
              </template>
            </s-goods-item>
          </view>
        </view>
      </view>
      <!-- 底部 -->
      <su-fixed v-if="state.list.length > 0" :isInset="false" :val="48" bottom placeholder>
        <view class="cart-footer ss-flex ss-col-center ss-row-between ss-p-x-30 border-bottom">
          <view class="footer-left ss-flex ss-col-center">
            <label class="check-box ss-flex ss-col-center ss-p-r-30" @tap="onSelectAll">
              <radio
                :checked="state.isAllSelected"
                color="var(--ui-BG-Main)"
                style="transform: scale(0.8)"
                @tap.stop="onSelectAll"
              />
              <view class="ss-m-l-8">{{ t('selectAll') }}</view>
            </label>
            <text>{{ t('total') }}：</text>
            <view class="text-price price-text">
              {{ fen2yuan(state.totalPriceSelected) }}
            </view>
          </view>
          <view class="footer-right">
            <button
              v-if="state.editMode"
              class="ss-reset-button ui-BG-Main-Gradient pay-btn ui-Shadow-Main"
              @tap="onDelete"
            >
              {{ t('delete') }}
            </button>
            <button
              v-else
              class="ss-reset-button ui-BG-Main-Gradient pay-btn ui-Shadow-Main"
              @tap="onConfirm"
            >
              {{ t('settlement') }}
              {{ state.selectedIds?.length ? `(${state.selectedIds.length})` : '' }}
            </button>
          </view>
        </view>
      </su-fixed>
    </view>
  </s-layout>
</template>

<script setup>
  import sheep from '@/sheep';
  import { onShow } from '@dcloudio/uni-app';
  import SpuApi from '@/sheep/api/product/spu';
  import { computed, reactive } from 'vue';
  import { fen2yuan } from '@/sheep/hooks/useGoods';
  import { isEmpty } from '@/sheep/helper/utils';
  import { useI18n } from '@/sheep/i18n';

  const { t } = useI18n('cart');

  // 隐藏原生tabBar
  uni.hideTabBar({
    fail: () => {},
  });

  const sys_navBar = sheep.$platform.navbar;
  const cart = sheep.$store('cart');

  const state = reactive({
    editMode: computed(() => cart.editMode),
    list: computed(() => cart.list),
    selectedList: [],
    selectedIds: computed(() => cart.selectedIds),
    isAllSelected: computed(() => cart.isAllSelected),
    totalPriceSelected: computed(() => cart.totalPriceSelected),
  });

  // 单选选中
  function onSelectSingle(id) {
    cart.selectSingle(id);
  }

  // 编辑、取消
  function onChangeEditMode(flag) {
    cart.onChangeEditMode(flag);
  }

  // 全选
  function onSelectAll() {
    cart.selectAll(!state.isAllSelected);
  }

  // 结算
  async function onConfirm() {
    const items = [];
    state.selectedList = state.list.filter((item) => state.selectedIds.includes(item.id));
    state.selectedList.map((item) => {
      // 此处前端做出修改
      items.push({
        skuId: item.sku.id,
        count: item.count,
        cartId: item.id,
        categoryId: item.spu.categoryId,
      });
    });
    if (isEmpty(items)) {
      sheep.$helper.toast(t('selectGoodsFirst'));
      return;
    }
    await validateDeliveryType(state.selectedList.map((item) => item.spu).map((spu) => spu.id));
    sheep.$router.go('/pages/order/confirm', {
      data: JSON.stringify({
        items,
      }),
    });
  }

  /**
   * 校验配送方式冲突
   *
   * @param {string[]} spuIds - 商品ID数组
   * @returns {Promise<void>}
   * @throws {Error} 当配送方式冲突或获取商品信息失败时抛出错误
   */
  async function validateDeliveryType(spuIds) {
    // 获取商品信息
    const { data: spuList } = await SpuApi.getSpuListByIds(spuIds.join(','));
    if (isEmpty(spuList)) {
      sheep.$helper.toast(t('goodsNotFound'));
      throw new Error(t('goodsNotFound'));
    }
    // 获取所有商品的配送方式列表
    const deliveryTypesList = spuList.map((item) => item.deliveryTypes);
    // 检查配送方式冲突
    const hasConflict = checkDeliveryConflicts(deliveryTypesList);
    if (hasConflict) {
      sheep.$helper.toast(t('deliveryConflict'));
      throw new Error(t('deliveryConflict'));
    }
  }

  /**
   * 检查配送方式列表中是否存在冲突
   * @description
   * 示例场景:
   * A 商品支持：[快递, 自提]
   * B 商品支持：[快递]
   * C 商品支持：[自提]
   *
   * 对比结果:
   * A 和 B：不冲突 (有交集：快递)
   * A 和 C：不冲突 (有交集：自提)
   * B 和 C：冲突 (无交集)
   * @param {Array<Array<number>>} deliveryTypesList - 配送方式列表的数组
   * @returns {boolean} 是否存在冲突
   */
  function checkDeliveryConflicts(deliveryTypesList) {
    for (let i = 0; i < deliveryTypesList.length - 1; i++) {
      const currentTypes = deliveryTypesList[i];
      for (let j = i + 1; j < deliveryTypesList.length; j++) {
        const nextTypes = deliveryTypesList[j];
        // 检查是否没有交集（即冲突）
        const hasNoIntersection = !currentTypes.some((type) => nextTypes.includes(type));
        if (hasNoIntersection) {
          return true;
        }
      }
    }
    return false;
  }

  function onNumberChange(e, cartItem) {
    if (e === 0) {
      cart.delete(cartItem.id);
      return;
    }
    if (cartItem.goods_num === e) return;
    cartItem.goods_num = e;
    cart.update({
      goods_id: cartItem.id,
      goods_num: e,
      goods_sku_price_id: cartItem.goods_sku_price_id,
    });
  }

  async function onDelete() {
    cart.delete(state.selectedIds);
  }

  function getCartList() {
    cart.getList();
  }

  onShow(() => {
    getCartList();
  });
</script>

<style lang="scss" scoped>
  :deep(.ui-fixed) {
    height: 72rpx;
  }

  .cart-box {
    width: 100%;

    .cart-header {
      height: 70rpx;
      background-color: #f6f6f6;
      width: 100%;
      position: fixed;
      left: 0;
      top: v-bind('sys_navBar') rpx;
      z-index: 1000;
      box-sizing: border-box;
    }

    .cart-footer {
      height: 100rpx;
      background-color: #fff;

      .pay-btn {
        width: 180rpx;
        height: 70rpx;
        font-size: 28rpx;
        line-height: 28rpx;
        font-weight: 500;
        border-radius: 40rpx;
      }
    }

    .cart-content {
      margin-top: 70rpx;

      .goods-box {
        background-color: #fff;
        position: relative;
      }
      // 下架商品
      .down-box {
        position: absolute;
        left: 0;
        top: 0;
        width: 100%;
        height: 100%;
        background: rgba(#fff, 0.8);
        z-index: 2;
        display: flex;
        justify-content: center;
        align-items: center;
        color: #999;
        font-size: 32rpx;
      }
    }
  }
</style>
