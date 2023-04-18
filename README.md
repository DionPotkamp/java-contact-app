# Java Contact App

This app is basically a copy of [this repo](https://github.com/DionPotkamp/java-todo-app). I have been working on that app during the SQLite classes.

This is an app where you keep track of your contacts, and allows you to Create, Read, Update, and Delete your contacts. Press on `Add Contact` to add a new contact. Only the name and the phone number are required. The contact will appear in the list.

The list of contacts can be refreshed, pull down to see the nice animation.

If you click on a contact, a detail screen will open (check out the cool scrolling feature). Click on the labels to open the link, hold down to copy.

On the main screen you can sort the list by clicking the `"a>z"` button. Long press on a contact and it will ask to delete it.

`MainActivity` has a `RecyclerView` ([contact_list_item](app/src/main/res/layout/contact_list_item.xml)). At the bottom of the screen, you can add a to-do item. When saved it is displayed in `MainActivity`.

`DBHelper` contains all the logic for creating and updating the SQLite database and does not have any logic.

`DBControl` contains methods to insert, update, delete, and select data from the database. It has the most minimal logic possible to keep it as general as possible.

The `Model` class has the most specific information, filled in by the extending class. And it has the **best feature**: If a model (something stored in the database) extends the class `Model` and correctly implements its abstract methods, saving and retrieving records is as easy as calling one method. `save()` (update & create), `delete()`, `get()`, and `getAll()`. And all while being fully SQL-injection save!
