# ItemVisibilityHelper [![](https://jitpack.io/v/mminng/item-visibility-helper.svg)](https://jitpack.io/#mminng/item-visibility-helper)

Item visibility helper for RecyclerView.  
Help implement video playlist, like Facebook, TikTok etc.  
Or other features.

# Screenshot

![screenshot](https://github.com/mminng/item-visibility-helper/blob/master/screenshots/simple.gif)

# Getting Started

**Step 1.** Add it in your root build.gradle at the end of repositories:

```Groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

**Step 2.** Add the dependency:

```Groovy
dependencies {
    ...
    implementation 'com.github.mminng:item-visibility-helper:1.0.3'
}
```

# Usage

```Kotlin
val helper: ItemVisibilityHelper = ItemVisibilityHelper()
//Attach to RecyclerView.
helper.attachToRecyclerView(recyclerView: RecyclerView,
                            targetViewId: Int = View.NO_ID,
                            autoActivate: Boolean = true,
                            orientation: Int = RecyclerView.VERTICAL,
                            reverseLayout: Boolean = false){
    activateItem { view, position ->
        //Item activated.
    }
    deactivateItem { view, position ->
        //Item deactivated.
    }
    pauseItem { view, position ->
        //Item paused.
    }
    resumeItem { view, position ->
        //Item resumed.
    }
}
//Activate most visible item.
helper.activateItem()
//Activate item by position.
helper.activateItem(position: Int)
//Deactivate item.
helper.deactivateItem()
```

# License

```markdown
Copyright 2023 mminng

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
