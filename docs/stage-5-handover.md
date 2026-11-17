# Stage 5: lists, request detail and external actions

Branch: `feature/5-lists-details-intents`, based on main `3c0e516`.

This is the Stage 5 implementation for integration with Stages 2 and 4. It is not a
standalone runnable application yet. No substitute ViewModels or navigation stubs are
included. The existing Hello World activity remains owned by Stage 4.

## Files and responsibilities

| File | Responsibility |
| --- | --- |
| `RequestAdapter.kt`, `row_request.xml` | Render saved requests and forward row clicks. DiffUtil compares IDs for identity and all fields for content. |
| `RequestsFragment.kt`, `fragment_requests.xml` | Observe requests, show loading/empty/list states, open detail by ID, start a new pickup. |
| `RequestDetailFragment.kt`, `fragment_request_detail.xml` | Load the requested row, show its actual data, start editing, delete after confirmation, open the calendar editor. |
| `DeleteRequestDialogFragment.kt` | Preserve delete confirmation over rotation; send a confirmed ID to the detail fragment. Keep request/cancel makes no database change. |
| `CentreAdapter.kt`, `row_centre.xml` | Render directory rows, honest unknown-value labels, and Maps action. Disable Maps when the query is absent. |
| `CentresFragment.kt`, `fragment_centres.xml` | City/device filters, loading/error/offline notice/retry/empty states. Keep cached rows visible during refresh. |
| `ExternalActions.kt` | Maps HTTPS URL and Calendar ACTION_INSERT intent, with feedback when no app can open the action. |
| `stage5_strings.xml` | Labels and messages for these screens. |

## Stage 2 handover

Supply the interfaces from the final team guide in `com.example.rudi.ui.viewmodel`:

- `Choices.cities`, `Choices.deviceFilter` (and the data layer's `deviceTypes`).
- `PickupViewModel`: `requests`, `selected`, `select(id)`, `busy`, `message`,
  `deletedId`, `delete(request)`, `consumeMessage()`, `consumeSaved()`.
- `DraftViewModel.clear("pickup")`, `fillFrom(request)`. This preserves the ID and
  original createdAt during edits.
- `CentreViewModel`: `city`, `device`, `centres`, `loading`, `error`, `notice`,
  `setCity`, `setDevice`, `prepare`. `centres` emits `CentreResult(city, cityEmpty, rows)`.
- `pickupWindow(date, slot)` and `nairobiZone` for calendar timestamps.

The detail fragment intentionally uses a **fragment-scoped** PickupViewModel for
selection and deletion, so a previous detail screen cannot deliver a stale row/null
to the next one. It still uses the **activity-scoped** DraftViewModel and
PickupViewModel when preparing the edit flow. Both scopes use the same Room database;
the request list updates through Room LiveData. No new ViewModel API is required.

`PickupViewModel.select` must distinguish a missing row from a failed database read
and report read errors, rather than leave a coroutine exception unhandled. This is
a Stage 2 responsibility to review at integration; the guide's selected API only
expresses a loaded row or null for missing data.

## Stage 4 handover

Provide the guide's existing signatures in `com.example.rudi.ui`:

```kotlin
MainActivity.openScreen(fragment: Fragment)
fun Fragment.screenTitle(title: String)
fun Fragment.startFlow(route: String) // Flow.kt; shared with Home
fun Spinner.bindChoices(values: List<String>, current: String, onPick: (String) -> Unit)
fun PickupRequest.title(): String
fun PickupRequest.slotText(): String
```

Provide `ItemFragment` as the first screen of the edit flow. Wire top-level navigation
to `RequestsFragment()` and `CentresFragment()`. A row opens
`RequestDetailFragment.newInstance(id: Long)`, using the saved Room ID.

On edit, Stage 5 fills the draft once and opens Item. Stage 4 must save using the
draft's original ID/createdAt, clear the draft after successful save, show Requests,
and clear the form back stack. Do not reset the draft on every Item view creation.

The details screen disables actions until the row is loaded, while delete is busy,
and while leaving. Only a matching successful deletedId navigates back. Error
messages do not trigger success navigation. Navigation after a result waits until
the fragment is resumed, avoiding transactions after state has been saved.

Stage 5 supplies all of its own XML layouts and resources. Do not recreate these
files in Stage 4. The shared Formatters, UiHelpers and Flow files remain Stage 4's.

## Verification status

- PASS: Android resource compilation using installed SDK Build Tools 35.0.0:
  `aapt2 compile --dir app/src/main/res -o build/stage5-check/resources.zip`.
- PASS: parsed all five new layouts, checked referenced View Binding IDs, checked
  XML/Kotlin string references and checked trailing whitespace.
- These checks do not establish Kotlin compilation or runtime correctness.
- Full build, lint, unit tests and device tests: **pending Stages 2/4 integration**.
- Main already lacks Choices. The earlier offline check could not resolve AGP
  8.7.2 from cache. The subsequent online check resolved the plugin and installed
  the missing Build Tools 34 and SDK Platform 35.
- The repository selects Gradle 9.6 / daemon Java 25, while the guide specifies
  Gradle 8.9 / JDK 17. Record the actual tested setup in the release README.

Keep this contribution on its feature branch, per the contributor's latest
instruction. Stage 4 has not merged as of the final branch check (main is still
3c0e516). The project guide recommends combining Stages 4/5 on `integration/ui`
after Stage 2. The complete app still requires the contracts above before it can
compile and run; this contribution is not a passing integration build.

## Stage 4 branch compatibility review

At review, `feature/stage4-app-shell` points to `253ab1b` (PR #2). It includes
temporary ViewModels and placeholder lists as well as the Stage 4 entry flow.
It is not on main. Reconcile these differences when integrating; do not blindly
accept both implementations:

- Keep Stage 5's `RequestsFragment.kt`, `CentresFragment.kt`,
  `fragment_requests.xml` and `fragment_centres.xml` as a matched set. Stage 4's
  placeholder lists have different binding IDs and do not implement Stage 5 detail.
- Stage 4's provisional PickupViewModel lacks `selected`, `select`, `busy`,
  `message`, `deletedId` and the consume functions. Stage 2 supplies the agreed
  ViewModel contract, including error and successful-deletion events.
- Stage 4's provisional DraftViewModel has plain fields and `clear()` only. The
  agreed Stage 2 implementation supplies SavedStateHandle, `clear(route)`,
  `fillFrom`, editingId and editingCreatedAt. Stage 4's form code must match it.
- Stage 4's Review currently constructs a new PickupRequest without the editing
  ID, so it would insert a duplicate on edit. Wire the agreed Stage 2 save flow.
- Stage 4's provisional CentreViewModel uses `observeFiltered`, `initData` and
  `refreshData` instead of the guide's result/filter/prepare/notice contract.
- Stage 4's current Flow.kt only opens a string route and does not clear/prefill
  a draft. Its `"pickup"` route skips Item; use the shared entry flow in the guide.
- Stage 4 provides `openScreen(Fragment)`, `title()` and `slotText()`. Its
  `bindChoices` signature also accepts this branch's arguments. Add the guide's
  `Fragment.screenTitle` helper (currently absent).

These are explicit integration tasks, not instructions to overwrite teammates'
work wholesale. No changes to Stage 4's branch are included in this contribution.

## Integration acceptance checklist (not yet executed)

- Create a pickup through Stage 4. Open it from Requests and verify every saved field.
- Edit its address/date. Save with the same ID; one updated row appears, no duplicate.
- Force stop and reopen; the updated request remains.
- Keep request / Back / outside-tap on delete confirmation leaves the row intact.
- Rotate with the confirmation open, then confirm; exactly one deletion occurs.
- Successful delete returns to Requests; restart confirms it stays removed.
- Failed deletion shows feedback, keeps detail open and permits retry.
- Open A, return, open B; no stale A content or false missing-row navigation.
- Rotate detail and background the app during deletion; no repeated action or crash.
- Empty Requests has explanatory text and a working New pickup request action.
- City/device filters survive returning from Guidance with the selected device.
- Nairobi + Laptop shows WEEE, with "Confirm with the centre" for unknown acceptance.
- Nakuru shows the city-empty message. A city with only nonmatching known types shows
  the device-empty message; All devices restores the rows.
- Directory refresh shows loading; offline keeps cached rows and shows notice/Retry.
  Reconnect and retry clears the notice. Local seed failure shows error, not false empty.
- Rows with no mapQuery have disabled Maps; other rows open Maps or browser.
- Calendar editor gets the saved address and Nairobi start/end times; the user saves
  the event. Unavailable calendar app shows feedback. Editing Room later does not
  update an existing calendar event.
- Back, rotation, keyboard and large-font checks pass after the combined UI is wired.

## Short presentation explanation

"My part is how we display and act on saved data. RequestsFragment observes the
ViewModel. Room emits new rows when data changes, and submitList passes them to the
adapter. DiffUtil uses the database ID to recognise the same request, and its field
values to decide whether to redraw it. Clicking a row passes only its ID to detail.
Editing fills the shared draft with that ID, so saving updates instead of inserting.
Delete asks for confirmation and returns to the list only on database success.
Centres uses the same list pattern, with filters and offline feedback. Maps and
Calendar are implicit intents: Android opens an appropriate app for the action."

Walk through: list -> detail -> edit -> same-ID save -> Keep request -> confirmed
delete -> empty list -> city/device filter -> Maps -> calendar editor.

References: final Rudi team guide, Stage 5 (pages 41-49);
[ListAdapter](https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter),
[fragment communication](https://developer.android.com/guide/fragments/communicate),
[DialogFragment](https://developer.android.com/guide/fragments/dialogs).
