package rek.remindme.ui.reminder

class ReminderListHelper {
    companion object {
        fun formatDescription(description: String): String {
            val inlineDescription = description.replace("\n", " ")
            val maxLength = 100

            if (inlineDescription.length > maxLength) {
                return "${inlineDescription.substring(0, maxLength)}..."
            }

            return inlineDescription
        }
    }
}