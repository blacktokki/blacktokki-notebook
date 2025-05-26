# 📘 User Guide - Blacktokki Notebook

**Blacktokki Notebook** is a markdown-based **knowledge and time management tool**. Users can freely create, structure, and track documents on various topics over time. Built on React Native, this app is optimized for both mobile and web environments and is ideal for **self-directed learning**, **work documentation**, **knowledge archiving**, and **time-centered retrospectives**.

### ✨ Key Features

* ✅ **Markdown-based editor** for flexible and intuitive writing
* 📁 **Hierarchical document structure** (supports folders and sub-documents)
* 📆 **Timeline feature** for date-based retrospectives and scheduling
* 🔁 **Paragraph moving functionality** for restructuring and refactoring documents
* 🧠 **Autocomplete links** to strengthen connections between documents
* 🕑 **TimerTag** for easy recurring schedule management
* 🧳 **Markdown import/export** for data backup and recovery

---

## 🏠 Home Screen Guide

### 📱 Mobile View

* **Top SearchBar**: Quickly search or create new documents at any time

* **Bottom Tab View**:

  * **Discovery**: Access recently viewed notes, problematic documents, and timeline
  * **All Notes**: View recently edited notes as cards
  * **Config**: Access settings, backups, and account management

### 🖥 Web View

* **Left-side Drawer Component**:

  * A fixed sidebar for accessing all tabs and menus
  * Shortcuts to "Recently Viewed Documents" and "Problematic Documents"

* **Right-side Content Area**:

  * Displays Notes, Timeline, or Config view depending on selected tab
  * UI optimized for web (e.g., right-click, long press actions)

---

## 📝 Viewing and Creating Notes

### ✏️ Creating a Document

* Enter a new title in the top search bar → a new document is automatically created if it doesn’t exist

### 🔍 Viewing a Document

* View **entire or partial content** based on titles and paragraphs
* 🧭 Use **paragraph navigation** buttons to explore structured content
* ✏️ Tap the **Edit button** to modify the document
* 📂 Use **TimerTag scheduling tools** to manage document-based events

### ✏️ Editing a Document

* Markdown-based editor available
* Autocomplete support:

  * `[Title`: Auto-completes links to other notes
  * `http://...`: Preview embedded links
* Automatically navigates to the document viewer upon saving

---

## 📌 Paragraph Moving Feature

* Move paragraphs to other documents or different positions
* Select the target document and paragraph to **partially copy or move** content
* Use **preview** to check changes before applying

---

## 🗓️ Timeline Feature

* View documents linked to specific dates
* Navigate dates or return to today using the top date navigation
* Click the displayed date to open a **calendar selector**

---

## 🛠 Settings (Config)

* 🔍 **Search Settings**:

  * View or delete previous search history

* 📦 **Export/Import**:

  * Backup notes as Markdown (ZIP format)
  * Import `.md` or `.zip` files to restore documents

* 👤 **Account Switching**:

  * Supports switching between local and external accounts

---

## 🧠 Document Types

| Type             | Description                                   |
| ---------------- | --------------------------------------------- |
| General Note     | Manually created user content                 |
| Recent Note      | Recently viewed or edited document            |
| Problematic Note | Empty or incomplete documents                 |
| Timeline Note    | Date-associated schedule-based notes          |
| Archived Note    | Snapshots of saved documents (with timestamp) |

---

## 🧭 Quick Start Guide

| Task            | Path                               |
| --------------- | ---------------------------------- |
| Search Document | Top 🔍 Search Bar                  |
| Create New Note | Enter non-existing title in search |
| Edit Document   | Viewer → ✏️ Edit Button            |
| Move Paragraph  | Viewer → 🔄 Icon                   |
| View Timeline   | Home → TimeLine Button             |
| Backup Notes    | Config Tab → Export                |
| Restore Notes   | Config Tab → Import                |

