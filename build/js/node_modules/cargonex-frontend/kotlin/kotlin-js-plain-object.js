(function (factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports'], factory);
  else if (typeof exports === 'object')
    factory(module.exports);
  else
    globalThis['kotlin-js-plain-object'] = factory(typeof globalThis['kotlin-js-plain-object'] === 'undefined' ? {} : globalThis['kotlin-js-plain-object']);
}(function (_) {
  'use strict';
  //region block: pre-declaration
  //endregion
  return _;
}));

//# sourceMappingURL=kotlin-js-plain-object.js.map
