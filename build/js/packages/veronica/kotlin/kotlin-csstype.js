(function (factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports'], factory);
  else if (typeof exports === 'object')
    factory(module.exports);
  else
    globalThis['kotlin-csstype'] = factory(typeof globalThis['kotlin-csstype'] === 'undefined' ? {} : globalThis['kotlin-csstype']);
}(function (_) {
  'use strict';
  //region block: pre-declaration
  //endregion
  return _;
}));

//# sourceMappingURL=kotlin-csstype.js.map
