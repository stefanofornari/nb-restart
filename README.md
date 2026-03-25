# Restart IDE — NetBeans Plugin

A minimal NetBeans plugin that adds a **Restart** action to the **File** menu and binds it to the **Ctrl+Shift+Backspace** shortcut.

![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)
![NetBeans](https://img.shields.io/badge/NetBeans-29%2B-green.svg)

---

## Features

| Feature | Detail |
|---|---|
| Menu entry | **File → Restart** (after a separator, at the very end) |
| Keyboard shortcut | **Ctrl + Shift + Backspace** |
| Icon | Circular-arrow restart icon in the menu item |
| Restart mechanism | `LifecycleManager.markForRestart()` + `exit()` (graceful save prompts preserved) |
| Fallback | Falls back to a normal `exit()` on platforms that don't support restart |

---

## Requirements

| Requirement | Version |
|---|---|
| NetBeans IDE | **29** or later |
| Java | **11** or later |
| Maven | **3.8** or later (for building) |

---

## Building

```bash
git clone https://github.com/stefanofornari/netbeans-restart-ide.git
cd netbeans-restart-ide
mvn package
```

The NBM (NetBeans Module) file will be produced at:

```
target/restart-ide-1.0.0.nbm
```

---

## Installation

### From the NBM file (manual)

1. Open NetBeans IDE.
2. Go to **Tools → Plugins → Downloaded**.
3. Click **Add Plugins…** and select `restart-ide-1.0.0.nbm`.
4. Click **Install** and follow the wizard.
5. Restart NetBeans when prompted.

### From the NetBeans Plugin Portal (once published)

1. Go to **Tools → Plugins → Available Plugins**.
2. Search for **Restart IDE**.
3. Select it and click **Install**.

---

## Usage

* Click **File → Restart IDE**, or
* Press **Ctrl + Shift + Backspace** anywhere in the IDE.

NetBeans will save open files (prompting for unsaved changes as usual) and then restart automatically.

---

## Publishing to the NetBeans Plugin Portal

Follow these steps to make the plugin available to the whole NetBeans community via the official [NetBeans Plugin Portal](https://plugins.netbeans.apache.org/).

### 1. Create an Apache ID (if you don't have one)

The Plugin Portal is hosted by the Apache Software Foundation.
Request an Apache ID at <https://id.apache.org/> or contribute as an external author by registering at <https://plugins.netbeans.apache.org/>.

### 2. Prepare the release

Ensure `pom.xml` contains accurate metadata:

```xml
<name>Restart</name>
<description>Adds a Restart action …</description>
<url>https://github.com/you/netbeans-restart-ide</url>
<licenses>
    <license>
        <name>Apache License, Version 2.0</name>
        <url>https://www.apache.org/licenses/LICENSE-2.0.txt</url>
    </license>
</licenses>
```

Build a clean release artifact:

```bash
mvn clean package
```

### 3. Tag the release in version control

```bash
git tag -a v1.0.0 -m "Release 1.0.0"
git push origin v1.0.0
```

### 4. Upload to the Plugin Portal

1. Log in at <https://plugins.netbeans.apache.org/>.
2. Click **Upload Plugin**.
3. Fill in the form:
   - **Plugin name**: `NetBeans Restart`
   - **Category**: `IDE Features` (or `Tools`)
   - **NBM file**: upload `target/restart-ide-1.0.0.nbm`
   - **Short description**: one-liner shown in the search results.
   - **Long description**: markdown or plain text shown on the plugin page.
   - **License**: `Apache-2.0`
   - **Source URL**: link to your GitHub repository.
   - **Minimum NB version**: `29`
4. Submit for review.  An Apache committer will review and approve the listing.

### 5. Update the listing for new releases

For subsequent releases, bump the `<version>` in `pom.xml`, rebuild, and upload the new NBM via **My Plugins → Edit → Upload new version** on the portal.

---

## How It Works

The plugin relies on the NetBeans **LifecycleManager** API:

```java
LifecycleManager lm = LifecycleManager.getDefault();
lm.markForRestart();   // schedules a restart after JVM exit
lm.exit();             // triggers normal IDE shutdown (save dialogs etc.)
```

`markForRestart()` is supported on the standard NetBeans launcher since NB 6.7.
On unsupported launchers the plugin silently falls back to `exit()`.

---

## License

Copyright 2025 Stefano Fornari
Released under the [Apache License, Version 2.0](LICENSE).

"Icons by [Font Awesome](https://fontawesome.com/v4/), licensed under CC BY 4.0."
