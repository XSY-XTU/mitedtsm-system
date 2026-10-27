<template>
	<view class="time" :style="justifyLeft">
		<text class="" v-if="tipText">{{ tipText }}</text>
		<text class="styleAll p6" v-if="isDay === true"
			:style="{background:bgColor.bgColor,color:bgColor.Color}">{{ day }}{{bgColor.isDay?dayText:''}}</text>
		<text class="timeTxt" v-if="dayText"
			:style="{width:bgColor.timeTxtwidth,color:bgColor.bgColor}">{{ dayText }}</text>
		<text class="styleAll" :class='isCol?"timeCol":""'
			:style="{background:bgColor.bgColor,color:bgColor.Color,width:bgColor.width}">{{ hour }}</text>
		<text class="timeTxt" v-if="hourText" :class='isCol?"whit":""'
			:style="{width:bgColor.timeTxtwidth,color:bgColor.bgColor}">{{ hourText }}</text>
		<text class="styleAll" :class='isCol?"timeCol":""'
			:style="{background:bgColor.bgColor,color:bgColor.Color,width:bgColor.width}">{{ minute }}</text>
		<text class="timeTxt" v-if="minuteText" :class='isCol?"whit":""'
			:style="{width:bgColor.timeTxtwidth,color:bgColor.bgColor}">{{ minuteText }}</text>
		<text class="styleAll" :class='isCol?"timeCol":""'
			:style="{background:bgColor.bgColor,color:bgColor.Color,width:bgColor.width}">{{ second }}</text>
		<text class="timeTxt" v-if="secondText">{{ secondText }}</text>
	</view>
</template>

<script>
	import { computed } from 'vue';
	import { useI18n } from '@/sheep/i18n';

	export default {
		name: "countDown",
		props: {
			justifyLeft: {
				type: String,
				default: ""
			},
			//距离开始提示文字
			tipText: {
				type: String,
				default: ""
			},
			dayText: {
				type: String,
				default: ""
			},
			hourText: {
				type: String,
				default: ""
			},
			minuteText: {
				type: String,
				default: ""
			},
			secondText: {
				type: String,
				default: ""
			},
			datatime: {
				type: Number,
				default: 0
			},
			isDay: {
				type: Boolean,
				default: true
			},
			isCol: {
				type: Boolean,
				default: false
			},
			bgColor: {
				type: Object,
				default: null
			}
		},
		setup(props) {
			const { t } = useI18n('activity');

			// 使用 i18n 获取翻译，如果 props 没有传值则使用默认值
			const tipText = computed(() => props.tipText || t('countdown'));
			const dayText = computed(() => props.dayText || t('days'));
			const hourText = computed(() => props.hourText || t('hours'));
			const minuteText = computed(() => props.minuteText || t('minutes'));
			const secondText = computed(() => props.secondText || t('seconds'));

			return {
				tipText,
				dayText,
				hourText,
				minuteText,
				secondText
			};
		},
		data: function() {
			return {
				day: "00",
				hour: "00",
				minute: "00",
				second: "00"
			};
		},
		created: function() {
			this.show_time();
		},
		mounted: function() {},
		methods: {
			show_time: function() {
				let that = this;

				function runTime() {
					//时间函数
					let intDiff = that.datatime - Date.parse(new Date()) / 1000; //获取数据中的时间戳的时间差；
					let day = 0,
						hour = 0,
						minute = 0,
						second = 0;
					if (intDiff > 0) {
						//转换时间
						if (that.isDay === true) {
							day = Math.floor(intDiff / (60 * 60 * 24));
						} else {
							day = 0;
						}
						hour = Math.floor(intDiff / (60 * 60)) - day * 24;
						minute = Math.floor(intDiff / 60) - day * 24 * 60 - hour * 60;
						second =
							Math.floor(intDiff) -
							day * 24 * 60 * 60 -
							hour * 60 * 60 -
							minute * 60;
						if (hour <= 9) hour = "0" + hour;
						if (minute <= 9) minute = "0" + minute;
						if (second <= 9) second = "0" + second;
						that.day = day;
						that.hour = hour;
						that.minute = minute;
						that.second = second;
					} else {
						that.day = "00";
						that.hour = "00";
						that.minute = "00";
						that.second = "00";
					}
				}
				runTime();
				setInterval(runTime, 1000);
			}
		}
	};
</script>

<style scoped>
	.p6 {
		padding: 0 8rpx;
	}

	.styleAll {
		/* color: #fff; */
		font-size: 24rpx;
		height: 36rpx;
		line-height: 36rpx;
		border-radius: 6rpx;
		text-align: center;
		/* padding: 0 6rpx; */
	}

	.timeTxt {
		text-align: center;
		/* width: 16rpx; */
		height: 36rpx;
		line-height: 36rpx;
		display: inline-block;
	}

	.whit {
		color: #fff !important;
	}

	.time {
		display: flex;
		justify-content: center;
	}

	.red {
		color: #fc4141;
		margin: 0 4rpx;
	}

	.timeCol {
		/* width: 40rpx;
		height: 40rpx;
		line-height: 40rpx;
		text-align:center;
		border-radius: 6px;
		background: #fff;
		font-size: 24rpx; */
		color: #E93323;
	}
</style>