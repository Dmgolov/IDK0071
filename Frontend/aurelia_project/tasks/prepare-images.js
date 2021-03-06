import gulp from 'gulp';
import changedInPlace from 'gulp-changed-in-place';
import project from '../aurelia.json';

export default function prepareImages() {

  const taskFonts = gulp.src('src/resources/images/*.{gif,jpg,jpeg,png}')
    .pipe(changedInPlace({ firstPass: true }))
    .pipe(gulp.dest(`${project.platform.output}/images`));

  return taskFonts;
}
