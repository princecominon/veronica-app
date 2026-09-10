(function (factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports', './kotlin-kotlin-stdlib.js', './kotlinx-coroutines-core.js'], factory);
  else if (typeof exports === 'object')
    factory(module.exports, require('./kotlin-kotlin-stdlib.js'), require('./kotlinx-coroutines-core.js'));
  else {
    if (typeof globalThis['kotlin-kotlin-stdlib'] === 'undefined') {
      throw new Error("Error loading module 'kotlin-js'. Its dependency 'kotlin-kotlin-stdlib' was not found. Please, check whether 'kotlin-kotlin-stdlib' is loaded prior to 'kotlin-js'.");
    }
    if (typeof globalThis['kotlinx-coroutines-core'] === 'undefined') {
      throw new Error("Error loading module 'kotlin-js'. Its dependency 'kotlinx-coroutines-core' was not found. Please, check whether 'kotlinx-coroutines-core' is loaded prior to 'kotlin-js'.");
    }
    globalThis['kotlin-js'] = factory(typeof globalThis['kotlin-js'] === 'undefined' ? {} : globalThis['kotlin-js'], globalThis['kotlin-kotlin-stdlib'], globalThis['kotlinx-coroutines-core']);
  }
}(function (_, kotlin_kotlin, kotlin_org_jetbrains_kotlinx_kotlinx_coroutines_core) {
  'use strict';
  //region block: imports
  var intercepted = kotlin_kotlin.$_$.n2;
  var CancellableContinuationImpl = kotlin_org_jetbrains_kotlinx_kotlinx_coroutines_core.$_$.c;
  var Unit_instance = kotlin_kotlin.$_$.j;
  var EmptyCoroutineContext_getInstance = kotlin_kotlin.$_$.g;
  var CoroutineScope = kotlin_org_jetbrains_kotlinx_kotlinx_coroutines_core.$_$.d;
  //endregion
  //region block: pre-declaration
  //endregion
  function awaitCancellation(block, $completion) {
    var cancellable = new CancellableContinuationImpl(intercepted($completion), 1);
    cancellable.zg();
    cancellable.oh(awaitCancellation$lambda(block));
    return cancellable.jh();
  }
  function awaitCancellation$lambda($block) {
    return function (it) {
      $block();
      return Unit_instance;
    };
  }
  function IsolatedCoroutineScope() {
    return CoroutineScope(EmptyCoroutineContext_getInstance());
  }
  //region block: exports
  _.$_$ = _.$_$ || {};
  _.$_$.a = awaitCancellation;
  _.$_$.b = IsolatedCoroutineScope;
  //endregion
  return _;
}));

//# sourceMappingURL=kotlin-js.js.map
