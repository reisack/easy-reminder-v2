package rek.remindme.common

import rek.remindme.R

class Consts {
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
        }
    }
}