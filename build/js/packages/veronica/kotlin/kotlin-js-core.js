(function (factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports'], factory);
  else if (typeof exports === 'object')
    factory(module.exports);
  else
    globalThis['kotlin-js-core'] = factory(typeof globalThis['kotlin-js-core'] === 'undefined' ? {} : globalThis['kotlin-js-core']);
}(function (_) {
  'use strict';
  //region block: pre-declaration
  //endregion
  return _;
}));

//# sourceMappingURL=kotlin-js-core.js.map
