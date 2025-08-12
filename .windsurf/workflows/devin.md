---
description: Send Task to Devin
---

Whenever I write "Send this to Devin" execute the actions below. 
Trigger
- Activate when the user writes **"can you send this to devin"**.

---

#### 2  Create specification directly
1. Summarise what the user wants to achieve in clear, concise prose (≈ ≤ 40 lines).
2. *If the request is unclear, pause and ask the user clarifying questions before continuing.*

---

#### 3  Wrap for encoding
1. Enclose the entire specification inside triple backticks:
   ```
   ```
   *specification text*
   ```
2. Ensure the full payload ≤ 4 kB (Devin's prompt limit).

---

#### 4  URL‑encode the prompt
Apply standard **percent‑encoding** to the back‑ticked block:
- space → `%20`
- line‑break → `%0A`
- `"` → `%22`
- *(continue for all reserved characters)*
** Do not use python or any other script just do this yourself

---

#### 5  Construct the Devin URL
```text
https://app.devin.ai/?prompt=<ENCODED_TEXT>&tags=Devin%20Search%20prompt
```
Replace `<ENCODED_TEXT>` with the output from **Step 4**.

---

#### 6  use Playwright MCP browser_navigate
- Launch a browser and navigate to the generated URL.
- Click the submit arrow contained within the DOM snippet below to send the prompt:

```html
<div class="-m-1 flex-row items-center justify-between ...">
  <!-- content truncated -->
  <div class="relative flex items-center gap-2 sm:min-w-0">
    <!-- gradient divs omitted -->
    <button class="flex size-[28px] items-center justify-center rounded-full p-1 text-black ...">
      <!-- Arrow SVG here -->
    </button>
  </div>
</div>
```