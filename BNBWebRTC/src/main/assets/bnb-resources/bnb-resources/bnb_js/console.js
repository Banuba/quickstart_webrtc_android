/**
 * Polyfill for browser's
 *
 * console.log
 * console.info
 * console.warn
 * console.error
 */

/***/

/** @param {...*} data */
const log = (...data) => {
  const str = data.map(String).join(" ")
  bnb.log(str)
}

const console = {
  info: log.bind(null, "\n[Console.INFO]"),
  log: log.bind(null, "\n[Console.LOG]"),
  warn: log.bind(null, "\n[Console.WARN]"),
  error: log.bind(null, "\n[Console.ERROR]"),
}

// JSC has it's own dummy `console`, overwrite it
if (globalThis.console !== console) {
  globalThis.console = console
}
