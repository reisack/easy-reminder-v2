package rek.remindme.common

import rek.remindme.R

class Consts {
    class System {
        companion object {
            const val APP_ID = "rek.remindme"
            const val ALARM_RECEIVER_ID = "rek.remindme.alarm-receiver"
        }
    }

    class Route {
        companion object {
            const val REMINDER_ID_NAV_ARG = "reminderId"
            const val MESSAGE_RES_NAV_ARG = "messageRes"
            const val PREFIX_LIST_REMINDER_ROUTE = "list?messageRes"
            const val ADD_REMINDER_ROUTE = "add"
            const val PREFIX_EDIT_REMINDER_ROUTE = "edit"
        }
    }

    class Validation {
        companion object {
            val mandatoryFieldsNotFilled = R.string.reminder_mandatory_fields_not_filled
            val setInPast = R.string.reminder_set_in_past
            val titleMaxCharactersReached = R.string.title_validation
            val descMaxCharactersReached = R.string.description_validation
        }
    }

    class TestTag {
        companion object {
            const val EMPTY_REMINDER_LIST_MESSAGE = "emptyReminderListMessage"
            const val ADD_REMINDER_BUTTON = "addReminderButton"
            const val INPUT_TIME_FIELD = "inputTimeField"
            const val INPUT_DATE_FIELD = "inputDateField"
            const val INPUT_TITLE_FIELD = "inputTitleField"
            const val INPUT_DESCRIPTION_FIELD = "inputDescriptionField"
            const val SAVE_BUTTON = "saveButton"
            const val MORE_VERT_BUTTON = "moreVertButton"
            const val CLEAR_NOTIFIED_REMINDERS_ITEM = "clearNotifiedRemindersItem"
            const val DELETE_REMINDER_BUTTON = "deleteReminderButton"
            const val CONFIRM_BUTTON = "confirmButton"
            const val CANCEL_BUTTON = "cancelButton"
            const val BACK_BUTTON = "backButton"
            const val SELECT_INPUT_MODE = "selectInputMode"
        }
    }
}