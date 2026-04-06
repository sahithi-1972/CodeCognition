const fs = require('fs');
const path = require('path');
const code = fs.readFileSync(path.join(__dirname, 'src/pages/Login.jsx'), 'utf8');
const lines = code.split('\n');
let stack = [];
const reTag = /<(\/)?([A-Za-z0-9_$]+)/g;
for (let i = 0; i < lines.length; i++) {
  let line = lines[i];
  let match;
  while ((match = reTag.exec(line)) !== null) {
    const closing = Boolean(match[1]);
    const tag = match[2];
    const rest = line.slice(match.index);
    const selfClosing = /<[^>]*\/>/.test(rest);
    if (closing) {
      if (stack.length === 0) {
        console.log('unmatched closing', tag, 'at line', i + 1, line.trim());
        continue;
      }
      const last = stack.pop();
      if (last !== tag) {
        console.log('tag mismatch at line', i + 1, `expected </${last}> but found </${tag}>`);
      }
    } else if (!selfClosing && tag !== 'input' && tag !== 'img' && tag !== 'path' && tag !== 'br' && tag !== 'hr' && tag !== 'AnimatePresence' && tag !== 'Shield' && tag !== 'Lock' && tag !== 'Eye' && tag !== 'EyeOff' && tag !== 'ArrowRight' && tag !== 'GitBranch' && tag !== 'Activity' && tag !== 'Zap' && tag !== 'AlertCircle' && tag !== 'Github' && tag !== 'Key' && tag !== 'CheckCircle' && tag !== 'span' && tag !== 'button' && tag !== 'form' && tag !== 'label' && tag !== 'div' && tag !== 'svg' && tag !== 'React.Fragment' && tag !== 'Fragment' && tag !== '></') {
      stack.push(tag);
    }
  }
}
console.log('stack len', stack.length, stack.slice(-20));
