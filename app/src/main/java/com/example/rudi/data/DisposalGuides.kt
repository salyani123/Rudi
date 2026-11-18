package com.example.rudi.data

data class GuideStep(val title: String, val detail: String)
data class Guidance(val steps: List<GuideStep>, val note: String?)

object DisposalGuides {
    private val check = GuideStep("Check acceptance", "Confirm drop-off requirements with the centre.")

    fun forItem(type: String, condition: String): Guidance {
        val steps = when (type) {
            "Laptop" -> listOf(
                GuideStep("Back up your files", "Keep a copy of anything you need."),
                GuideStep("Remove personal data", "Follow your device manufacturer's instructions."),
                GuideStep("Remove personal accessories", "Take out removable memory cards and USB drives."),
                check
            )
            "Phone" -> listOf(
                GuideStep("Back up your data", "Keep a copy of photos, contacts and files."),
                GuideStep("Erase personal data", "Follow your phone manufacturer's reset instructions."),
                GuideStep("Remove SIM and memory card", "Keep them or dispose of them separately."),
                check
            )
            "Charger" -> listOf(
                GuideStep("Disconnect from power", "Unplug the charger and let it cool."),
                GuideStep("Keep the cable with it", "Hand over the charger and cable together."),
                check
            )
            else -> listOf(
                GuideStep("Unplug and let it cool", "Disconnect the appliance from power."),
                GuideStep("Follow the maker's advice", "Check the manufacturer's disposal instructions."),
                check
            )
        }
        val note = when {
            type !in listOf("Laptop", "Phone") -> null
            condition == "Damaged" -> "If the battery is swollen, leaking or hot, do not charge or open the device. Ask the manufacturer or recycler how to handle it before you travel."
            condition != "Working" -> "Damaged battery? Ask the recycler for handling advice."
            else -> null
        }
        return Guidance(steps, note)
    }
}
