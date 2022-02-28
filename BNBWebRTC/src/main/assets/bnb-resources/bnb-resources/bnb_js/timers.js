/**
 * Polyfill for browser's
 * 
 * setTimeout
 * clearTimeout
 * setInterval
 * clearInterval
 */

/***/

const id = (() => {
  let id = 0
  return () => id++
})()

const eventLoop = {
  /**
   * @param {Function} listener
   * @returns {Function} unsubscribe
   */
  subscribe: (listener) => {
    bnb.eventListener.on("onUpdate", listener)

    return () => {
      bnb.eventListener.off("onUpdate", listener)
    }
  }
}

/** @type {Map<number, Function>} */
const timers = new Map()

/**
 * @param {Function} callback - A function to be executyed every `delay` milliseconds.
 * @param {number} [delay] - The time, in milliseconds (thousandths of a second), the timer should delay in between executions of the specified function.
 * @param {...*} args - Additional arguments which are passed through to the function specified by func once the timer expires.
 * @returns {number} intervalID - The returned intervalID is a numeric, non-zero value which identifies the timer created by the call to setInterval();
 * this value can be passed to {@link clearInterval} to cancel the interval.
 * 
 * @see https://developer.mozilla.org/en-US/docs/Web/API/WindowOrWorkerGlobalScope/setInterval
 */
const setInterval = (callback, delay = 0, ...args) => {
  let start = Date.now()

  const intervalId = id()
  const unsubscribe = eventLoop.subscribe(() => {
    if (Date.now() - start < delay) return

    start = Date.now()

    callback(...args)
  })

  timers.set(intervalId, unsubscribe)

  return intervalId
}

/**
 * @param {number} intervalID - The identifier of the repeated action you want to cancel.
 * This ID was returned by the corresponding call to {@link setInterval}.
 */
const clearInterval = (intervalID) => {
  const unsubscribe = timers.get(intervalID)

  if (unsubscribe) unsubscribe()

  timers.delete(intervalID)
}

/**
 * @param {Function} callback - A function to be executyed after the timer expires.
 * @param {number} [delay] - The time, in milliseconds that the timer should wait before the specified function or code is executed.
 * If this parameter is omitted, a value of 0 is used, meaning execute "immediately", or more accurately, the next event cycle.
 * @param {...*} args - Additional arguments which are passed through to the function specified by func once the timer expires.
 * @returns {number} timeoutID - The returned timeoutID is a positive integer value which identifies the timer created by the call to setTimeout().
 * This value can be passed to {@link clearTimeout} to cancel the timeout.
 * 
 * @see https://developer.mozilla.org/en-US/docs/Web/API/WindowOrWorkerGlobalScope/setTimeout
 */
const setTimeout = (callback, delay = 0, ...args) => {
  const timeoutID = setInterval(() => {
    clearInterval(timeoutID)

    callback(...args)
  }, delay)

  return timeoutID
}

/**
 * @param {number} timeoutID - The identifier of the timeout you want to cancel.
 * This ID was returned by the corresponding call to {@link setTimeout}.
 */
const clearTimeout = clearInterval

if (!globalThis.setInterval && !globalThis.clearInterval) {
  globalThis.setInterval = setInterval
  globalThis.clearInterval = clearInterval
}

if (!globalThis.setTimeout && !globalThis.clearTimeout) {
  globalThis.setTimeout = setTimeout
  globalThis.clearTimeout = clearTimeout
}