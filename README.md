**Project SmartTime**

1. Commiting Changes

* When commiting your own changes, do not commit directly to the master branch.
* Commit changes to your own branch (ex: "dev/tharindu").
* After committing changes create a PR (Pull Request) assigning one of our team members.
* Once the PR gets accepted, developer should merge the branch to the master branch.

**IMPORTANT**

* Commit message should be in a format like this

[Task-Number]:[Space][What change you exactly made on the project]

Example: (LN-5: Created the UI for MainActivity)

2. Merging to the master

* The PR should be accepted in order to merge the branch to master.
* Merging must be done by the developer.

**IMPORTANT**

* The merge message will initially look like following

Merged in dev/tharindu (pull request #1)

Added DetailActivity and ToDoListActivity

Approved-by: Tharindu Roshana Ranaweera <ranaweerat@gmail.com>

But in the GIT client the merge will be shown with one line "Merged in dev/tharindu (pull request #1)"
which does not make any sense. So in order to make it meaningful, please update the message as follows.

Added DetailActivity and ToDoListActivity

Merged in dev/tharindu (pull request #1)

Approved-by: Tharindu Roshana Ranaweera <ranaweerat@gmail.com>

