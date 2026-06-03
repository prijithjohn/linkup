console.log("LinkUp AI Assistant Loaded Successfully");

let currentPopup = null;

const LINKEDIN_FOOTER_SELECTORS = [
    '.msg-messaging-form__footer',
    '.msg-form__footer',
    '.msg-form__left-aligned-actions',
    '.msg-form__right-aligned-actions'
];

const LINKEDIN_CHAT_INPUT = '.msg-messaging-form__editable-content, .msg-form__contenteditable, [contenteditable="true"]';
const LINKEDIN_MESSAGE_BUBBLE = '.msg-s-message-list-item__body, .msg-s-event-listitem__body';
const LINKEDIN_HEADER_NAME = '.msg-entity-lockup__title, .msg-overlay-conversation-bubble__name, .msg-thread-header__title';


function createAIButton() {
    const button = document.createElement('div');

    button.className = 'ai-linkedin-reply-trigger-btn';
    button.innerHTML = '✨ AI Reply';

    Object.assign(button.style, {
        margin: '0 10px',
        padding: '6px 14px',
        borderRadius: '16px',
        fontSize: '12px',
        fontFamily: '-apple-system, system-ui, sans-serif',
        fontWeight: '600',
        cursor: 'pointer',
        background: '#0a66c2',
        color: 'white',
        border: 'none',
        display: 'inline-block'
    });

    button.setAttribute('role', 'button');
    button.setAttribute('title', 'Generate AI Reply');

    return button;
}


async function callAI(emailContent, tone = "Professional", recipientName = "") {

    const payload = {
        messageContent: emailContent,
        tone,
        recipientName,
        targetRole: "Professional",
        targetCompany: ""
    };

    console.log("LinkUp AI request payload", payload);

    const response = await fetch('http://localhost:8086/api/linkedin/fast-reply', {
        method: 'POST',
        mode: 'cors',
        cache: 'no-cache',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
    });

    const responseText = await response.text();

    if (!response.ok) {
        console.error(`LinkUp AI backend error: ${response.status} ${response.statusText}`, responseText);
        throw new Error(`AI backend error ${response.status}: ${responseText || response.statusText}`);
    }

    let data;
    try {
        data = JSON.parse(responseText);
    } catch (parseError) {
        console.error('LinkUp AI response JSON parse failed', parseError, responseText);
        throw new Error('Failed to parse AI response');
    }

    console.log('LinkUp AI response data', data);

    if (!data || typeof data.reply !== 'string') {
        throw new Error('Invalid AI response format');
    }

    return data.reply;
}


function getChatContext(wrapper) {
    if (!wrapper || wrapper === document) {
        const messages = document.querySelectorAll(LINKEDIN_MESSAGE_BUBBLE);
        return messages.length ? messages[messages.length - 1].innerText.trim() : '';
    }

    const messages = wrapper.querySelectorAll(LINKEDIN_MESSAGE_BUBBLE);
    return messages.length ? messages[messages.length - 1].innerText.trim() : '';
}

function getRecipientName(wrapper) {
    const nameEl = document.querySelector(LINKEDIN_HEADER_NAME);
    if (!nameEl) return '';
    return nameEl.innerText.trim().split(' ')[0];
}

function getComposeBox(wrapper) {
    if (!wrapper || wrapper === document) {
        return document.querySelector(LINKEDIN_CHAT_INPUT);
    }
    return wrapper.querySelector(LINKEDIN_CHAT_INPUT);
}


function createPopup(text, wrapper, context) {

    console.log('LinkUp AI popup created', { text, context });

    if (currentPopup) currentPopup.remove();

    let currentText = text;
    let minimized = false;

    const popup = document.createElement("div");

    Object.assign(popup.style, {
        position: "fixed",
        width: "340px",
        maxHeight: "420px",
        background: "rgba(30,30,30,0.92)",
        backdropFilter: "blur(10px)",
        color: "white",
        borderRadius: "12px",
        zIndex: "999999",
        boxShadow: "0 12px 30px rgba(0,0,0,0.5)",
        fontSize: "13px",
        top: "120px",
        left: "120px",
        overflow: "hidden"
    });

    popup.innerHTML = `
        <div id="header" style="display:flex;justify-content:space-between;padding:10px;background:#1f1f1f;cursor:move;">
            <span style="color:#0a66c2;font-weight:600;">LinkUp AI</span>
            <button id="close" style="background:none;border:none;color:#aaa;">✕</button>
        </div>

        <div id="body" style="padding:12px;">
            <div id="content" style="white-space:pre-wrap;max-height:160px;overflow:auto;"></div>

            <select id="tone" style="width:100%;margin-top:10px;padding:6px;">
                <option>Professional</option>
                <option>Confident</option>
                <option>Friendly</option>
                <option>Concise</option>
            </select>

            <div style="display:flex;gap:8px;margin-top:10px;">
                <button id="insert" style="flex:1;background:#0a66c2;color:white;padding:6px;">Insert</button>
                <button id="copy" style="flex:1;background:#333;color:white;padding:6px;">Copy</button>
            </div>

            <button id="regen" style="margin-top:10px;width:100%;background:#e07a5f;color:white;padding:6px;">
                Regenerate
            </button>
        </div>
    `;

    document.body.appendChild(popup);

    const content = popup.querySelector("#content");
    content.innerText = text;

    currentPopup = popup;

    // close
    popup.querySelector("#close").onclick = () => popup.remove();

    // copy
    popup.querySelector("#copy").onclick = () => {
        navigator.clipboard.writeText(currentText);
    };

    // insert
    popup.querySelector("#insert").onclick = () => {
        const box = getComposeBox(wrapper);
        if (!box) return alert("Input not found");

        box.focus();
        document.execCommand("insertText", false, currentText);
        popup.remove();
    };

    // regenerate 
    popup.querySelector("#regen").onclick = async () => {
        const tone = popup.querySelector("#tone").value;

        content.innerText = "Regenerating...";

        const newReply = await callAI(context, tone, getRecipientName(wrapper));

        currentText = newReply;
        content.innerText = newReply;
    };

    return popup;
}


function injectButton() {
    console.log("LinkUp AI injectButton invoked");

    let toolbars = [];

    for (const selector of LINKEDIN_FOOTER_SELECTORS) {
        const found = document.querySelectorAll(selector);
        if (found.length) {
            toolbars = Array.from(found);
            break;
        }
    }

    toolbars.forEach(toolbar => {

        if (toolbar.querySelector('.ai-linkedin-reply-trigger-btn')) return;

        const wrapper =
            toolbar.closest('.msg-convo-wrapper') ||
            toolbar.closest('.msg-overlay-conversation-bubble') ||
            document;

        const button = createAIButton();

        button.onclick = async (e) => {
            e.preventDefault();
            e.stopPropagation();

            try {
                button.innerHTML = "⏳";
                button.style.background = "#333";

                const context = getChatContext(wrapper);
                const name = getRecipientName(wrapper);

                const reply = await callAI(
                    context || "Hello",
                    "Professional",
                    name
                );

                createPopup(reply, wrapper, context);

            } catch (err) {
                console.error('LinkUp AI request failed', err);
                alert("AI failed: " + (err.message || "Unknown error"));
            } finally {
                button.innerHTML = "✨ AI Reply";
                button.style.background = "#0a66c2";
            }
        };

        toolbar.appendChild(button);
    });
}


const observer = new MutationObserver(() => injectButton());

injectButton();

observer.observe(document.body, {
    childList: true,
    subtree: true
});
