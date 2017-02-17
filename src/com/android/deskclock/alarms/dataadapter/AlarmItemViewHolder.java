/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.deskclock.alarms.dataadapter;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.android.deskclock.AlarmUtils;
import com.android.deskclock.ItemAdapter;
import com.android.deskclock.R;
import com.android.deskclock.provider.Alarm;
import com.android.deskclock.provider.AlarmInstance;
import com.android.deskclock.widget.TextTime;

/**
 * Abstract ViewHolder for alarm time items.
 */
public abstract class AlarmItemViewHolder extends ItemAdapter.ItemViewHolder<AlarmItemHolder> {

    private static final float CLOCK_ENABLED_ALPHA = 1f;
    private static final float CLOCK_DISABLED_ALPHA = 0.69f;

    public final TextTime clock;
    public final CompoundButton onOff;
    public final View arrow;
    public final View preemptiveDismissContainer;
    public final TextView preemptiveDismissButton;

    public AlarmItemViewHolder(View itemView) {
        super(itemView);

        clock = (TextTime) itemView.findViewById(R.id.digital_clock);
        onOff = (CompoundButton) itemView.findViewById(R.id.onoff);
        arrow = itemView.findViewById(R.id.arrow);
        preemptiveDismissContainer = itemView.findViewById(R.id.preemptive_dismiss_container);
        preemptiveDismissButton =
                (TextView) itemView.findViewById(R.id.preemptive_dismiss_button);
        preemptiveDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlarmInstance alarmInstance = getItemHolder().getAlarmInstance();
                if (alarmInstance != null) {
                    getItemHolder().getAlarmTimeClickHandler().dismissAlarmInstance(alarmInstance);
                }
            }
        });
        onOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                getItemHolder().getAlarmTimeClickHandler().setAlarmEnabled(
                        getItemHolder().item, checked);
            }
        });
    }

    @Override
    protected void onBindItemView(final AlarmItemHolder itemHolder) {
        final Alarm alarm = itemHolder.item;
        bindOnOffSwitch(alarm);
        bindClock(alarm);
        final Context context = itemView.getContext();
        itemView.setContentDescription(clock.getText() + " " + alarm.getLabelOrDefault(context));
    }

    protected void bindOnOffSwitch(Alarm alarm) {
        if (onOff.isChecked() != alarm.enabled) {
            onOff.setChecked(alarm.enabled);
        }
    }

    protected void bindClock(Alarm alarm) {
        clock.setTime(alarm.hour, alarm.minutes);
        clock.setAlpha(alarm.enabled ? CLOCK_ENABLED_ALPHA : CLOCK_DISABLED_ALPHA);
    }

    protected boolean bindPreemptiveDismissButton(Context context, Alarm alarm,
            AlarmInstance alarmInstance) {
        final boolean canBind = alarm.canPreemptivelyDismiss() && alarmInstance != null;
        if (canBind) {
            preemptiveDismissContainer.setVisibility(View.VISIBLE);
            final String dismissText = alarm.instanceState == AlarmInstance.SNOOZE_STATE
                    ? context.getString(R.string.alarm_alert_snooze_until,
                            AlarmUtils.getAlarmText(context, alarmInstance, false))
                    : context.getString(R.string.alarm_alert_dismiss_text);
            preemptiveDismissButton.setText(dismissText);
            preemptiveDismissButton.setClickable(true);
        } else {
            preemptiveDismissContainer.setVisibility(View.GONE);
            preemptiveDismissButton.setClickable(false);
        }
        return canBind;
    }
}