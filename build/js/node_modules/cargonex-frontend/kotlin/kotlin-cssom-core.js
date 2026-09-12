(function (factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports'], factory);
  else if (typeof exports === 'object')
    factory(module.exports);
  else
    globalThis['kotlin-cssom-core'] = factory(typeof globalThis['kotlin-cssom-core'] === 'undefined' ? {} : globalThis['kotlin-cssom-core']);
}(function (_) {
  'use strict';
  //region block: pre-declaration
  //endregion
  return _;
}));

//# sourceMappingURL=kotlin-cssom-core.js.map
