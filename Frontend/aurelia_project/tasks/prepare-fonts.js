import gulp from 'gulp';
import changedInPlace from 'gulp-changed-in-place';
import project from '../aurelia.json';

export default function prepareFonts() {

  const taskFonts = gulp.src('src/resources/fonts/*otf')
    .pipe(changedInPlace({ firstPass: true }))
    .pipe(gulp.dest(`${project.platform.output}/fonts`));

  return taskFonts;
}
